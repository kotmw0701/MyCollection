package jp.kotmw.together.bossmonster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.Main;
import jp.kotmw.together.bossmonster.skills.FocusAttack;
import jp.kotmw.together.bossmonster.skills.Instant_Death;
import jp.kotmw.together.bossmonster.skills.PowerUpAncestors;
import jp.kotmw.together.bossmonster.skills.RotationAttack;
import jp.kotmw.together.bossmonster.skills.SkillBase;
import jp.kotmw.together.bossmonster.skills.TargetBeam;
import jp.kotmw.together.util.Title;

public class Boss extends BukkitRunnable implements Listener{

	private String name;
	private double maxbosshelth = 1000;
	private List<UUID> challengers = new ArrayList<>();
	private Map<UUID, Double> hate = new HashMap<>();
	private Map<UUID, Double> dps = new HashMap<>();
	private Creature boss;
	private BossBar bossbar;
	private Location center;
	private SkillBase Askill;
	private SkillBase Bskill;
	private Shield shield;
	private Pattern pattern = Pattern.a;
	private int level = 1;
	private int tick = (20*60)*20+3*20;//20分？+3秒
	private int timecount;
	private int togglesecond = 10;
	private boolean playerturn = true;
	private boolean isstarted;
	public static final String BOSS_PREFIX = "[BossSystem"+(Main.bossdebug ? " - DebugMode" : "")+"] ";	
	
	public Boss(Location loc, String name) {
		this.center = loc;
		this.name = name;
		//resetShield();
		bossbar = Bukkit.createBossBar(ChatColor.GOLD.toString()+ChatColor.BOLD+"Lv."+level+"  "+name+ChatColor.RESET, BarColor.GREEN, BarStyle.SEGMENTED_20, BarFlag.DARKEN_SKY);
		Bukkit.getOnlinePlayers().stream().filter(player -> (loc.distance(player.getLocation()) <= 20)).forEach(player -> {
			challengers.add(player.getUniqueId());
			bossbar.addPlayer(player);
		});
		maxbosshelth += (challengers.size()-1)*100;
		if(maxbosshelth >= 2000)
			maxbosshelth = 2000;
		boss = (Creature) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
		boss.setAI(false);
		boss.setCustomName(ChatColor.GOLD.toString()+ChatColor.BOLD+"Lv."+level+"  "+name+ChatColor.RESET);
		boss.setCustomNameVisible(true);
		boss.setMaxHealth(maxbosshelth);
		boss.setHealth(maxbosshelth);
		boss.setRemoveWhenFarAway(false);
		ItemStack item = new ItemStack(Material.LEATHER_HELMET);
		ItemMeta meta = item.getItemMeta();
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		boss.getEquipment().setHelmet(item);
		Zombie zombie = (Zombie) boss;
		zombie.setBaby(false);
		AttributeInstance gkr = boss.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
		AttributeInstance gad = boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
		gkr.addModifier(new AttributeModifier("generic.knockbackResistance", 1.0, Operation.ADD_NUMBER));
		gad.addModifier(new AttributeModifier("generic.attackDamage", getDiffDamage(10.0), Operation.ADD_NUMBER));
		sendChallengersMessage(ChatColor.GREEN+"概要: "+ChatColor.GOLD+"あなた達はボスの対戦者として設定されました、ボスを倒す、若しくは、死ぬまでここからは出られません");
		if(Main.bossdebug) {
			sendChallengersMessage("デバッグモードが有効になっています");
			challengers.forEach(player -> sendChallengersMessage(Bukkit.getPlayer(player).getName()));
		}
	}
	
	public void start() {
		if(challengers.isEmpty())
			throw new NullPointerException("参加者が存在していません (  ´∀｀)＜ぬるぽ");
		challengers.forEach(player -> hate.put(player, 0.0));
		/*Boss_Exception be = new Boss_Exception(this);
		if(be.checkException()) {
			be.start();
			return;
		}*/
		this.isstarted = true;
		boss.setAI(true);
		sendChallengersMessage("戦闘開始");
		this.runTaskTimer(Main.instance, 0, 1);
	}
	
	public void restart() {
		this.isstarted = true;
		boss.setAI(true);
		sendChallengersMessage("戦闘開始");
		this.runTaskTimer(Main.instance, 0, 1);
	}
	
	public void exceptionstart() {
		this.isstarted = true;
		boss.setAI(true);
		sendChallengersMessage(ChatColor.RED+"修正不可能なため、強制的に戦闘を開始します、対象を殲滅してください");
		this.runTaskTimer(Main.instance, 0, 1);
		
	}
	
	public void kill() {
		boss.remove();
		if(!isstarted) getchallengers().forEach(player -> bossbar.removeAll());
		if(shield != null) shield.cancel();
	}

	@Override
	public void run() {
		if(boss.isDead()) {
			this.cancel();
			sendChallengersMessage("討伐成功！");
			getchallengers().forEach(player -> bossbar.removeAll());
			return;
		}
		if(tick > 20*3) {
			updateHPGauge();
			if(pattern.equals(Pattern.a) && ((boss.getHealth()/boss.getMaxHealth())*10 <= 8 )) {
				pattern = Pattern.b;
				Bskill = new PowerUpAncestors(this, 2);
			} else if(pattern.equals(Pattern.b) && ((boss.getHealth()/boss.getMaxHealth())*10 <= 5)) {
				pattern = Pattern.c;
				Bskill = new PowerUpAncestors(this, 4);
			}
			else if(pattern.equals(Pattern.c) && ((boss.getHealth()/boss.getMaxHealth())*10 <= 2)) pattern = Pattern.d;
			if(tick%20 == 0) {
				if(timecount%2 == 0) {
					if(Askill == null || !Askill.isRunning()) {
						Random random = new Random();
						switch(random.nextInt(10)) {
						case 0:
							Askill = new RotationAttack(this);
							break;
						case 1:
							Askill = new FocusAttack(this);
							break;
						case 2:
							Askill = new TargetBeam(this);
							break;
						}
					}
				}
				if(timecount >= togglesecond) {
					timecount = 0;
					settingPlayerHate();
					boss.setTarget(getHatePlayer(1));
				}
				timecount++;
			}
		} else {
			if(tick%20 == 0) {
				if(tick/20 == 3) {
					sendChallengersMessage("時間切れの為、即死攻撃を実行し、戦闘を強制終了します");
					sendChallengersMessage("ボス残HP: "+boss.getHealth());
					if(Askill != null && Askill.isRunning())
						Askill.cancel();
					if(Bskill != null && Bskill.isRunning())
						Bskill.cancel();
					new Instant_Death(this);
					this.cancel();
				}
			}
		}
		tick--;
	}
	
	public void sendChallengersText(String text) {
		challengers.forEach(player -> Bukkit.getPlayer(player).sendMessage(text));
	}
	
	public void sendChallengersMessage(String msg) {
		challengers.forEach(player -> Bukkit.getPlayer(player).sendMessage(BOSS_PREFIX+msg));
	}
	
	public void sendChallengersTitle(int fadein, int stay, int fadeout, String main, String sub) {
		challengers.forEach(player -> Title.sendTitle(Bukkit.getPlayer(player), fadein, stay, fadeout, main, sub));
	}
	
	public boolean isStarted() {
		return isstarted;
	}
	
	public boolean isPlayerturn() {
		return playerturn;
	}
	
	public boolean isChallenger(Player player) {
		return challengers.contains(player.getUniqueId());
	}
	
	public boolean isBoss(Entity entity) {
		return boss.getUniqueId().equals(entity.getUniqueId());
	}
	
	public List<Player> getchallengers() {
		List<Player> challengers = new ArrayList<>();
		this.challengers.forEach(player -> challengers.add(Bukkit.getPlayer(player)));
		return challengers;
	}
	
	public Location getCenterLocation() {
		return center;
	}
	
	public Creature getBoss() {
		return boss;
	}
	
	public Shield getShield() {
		return shield;
	}
	
	public Pattern getPattern() {
		return pattern;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setPlayerTurn(boolean playerturn) {
		if((Bskill != null) && (Bskill.isRunning()))//DPSチェックの時だけは攻撃可能にするため
			return;
		this.playerturn = playerturn;
		if(!playerturn) {
			if(shield != null && shield.isRunning())
				return;
			Shield shield = new Shield(this, 5);
			shield.runTaskTimer(Main.instance, 0, 1);
			this.shield = shield;
		} else {
			if(shield == null)
				return;
			shield.cancel();
		}
	}
	
	public void addDamage(UUID player, double damage) {
		if(!dps.containsKey(player)) {
			dps.put(player, damage);
			return;
		}
		dps.put(player, dps.get(player)+damage);
	}
	
	public Player getHatePlayer(int num) {
		List<UUID> sorted = new ArrayList<>();
		hate.entrySet().stream()
		.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
		.forEach(hates -> sorted.add(hates.getKey()));
		return Bukkit.getPlayer(sorted.get(num-1));
	}
	
	public double getDiffDamage(double basedamage) {
		switch(boss.getWorld().getDifficulty()) {
		case EASY:
			basedamage *= 2;
			break;
		case HARD:
			basedamage *= 0.5;
			break;
		case NORMAL:
			break;
		default:
			break;
		}
		return basedamage;
	}
	
	public void leavePlayer(String player) {
		bossbar.removePlayer(Bukkit.getPlayer(player));
		challengers.remove(player);
		hate.remove(player);
		dps.remove(player);
		if(challengers.size() < 1) {
			this.cancel();
			boss.remove();
			bossbar.removeAll();
		}
	}
	
	//同じパラメータが居た場合ちょっと調整
	private void settingPlayerHate() {
		dps.entrySet().forEach(data -> {
			BigDecimal bd = new BigDecimal(data.getValue().doubleValue()/togglesecond).setScale(2, BigDecimal.ROUND_DOWN);
			hate.put(data.getKey(), bd.doubleValue()+(hate.values().contains(bd.doubleValue()) ? 0.01 : 0.0));
		});
		if(Main.bossdebug) {
			sendChallengersMessage("ボスの残HP: "+boss.getHealth());
			sendChallengersMessage("Playerそれぞれのヘイトパラメーター");
			hate.entrySet().forEach(data -> sendChallengersMessage(data.getKey()+" : "+data.getValue()));
		}
		dps.clear();
	}
	
	/*
	 * a 100%
	 * b 80%
	 * c 50%
	 * d 20%
	 */
	public enum Pattern {
		a,b,c,d,Exception;
	}
	
	public void updateHPGauge() {
		double progless = boss.getHealth() / boss.getMaxHealth();
		bossbar.setProgress(progless);
		bossbar.setTitle(ChatColor.GOLD.toString()+ChatColor.BOLD+((int)Math.floor(progless*100))+"% Lv."+level+"  "+name+ChatColor.RESET);
		boss.setCustomName(ChatColor.GOLD.toString()+ChatColor.BOLD+((int)Math.floor(progless*100))+"% Lv."+level+"  "+name+ChatColor.RESET);
		if(progless*10 > 5) bossbar.setColor(BarColor.GREEN);
		else if((progless*10 <= 5) && (progless > 2)) bossbar.setColor(BarColor.YELLOW);
		else if(progless*10 <= 2) bossbar.setColor(BarColor.RED);
	}
	
	@SuppressWarnings("unused")
	private void resetShield() {
		Bukkit.getWorlds().forEach(world ->{
			world.getEntities().stream()
			.filter(entity -> entity.getType() == EntityType.ARMOR_STAND)
			.filter(entity -> entity.getCustomName().equalsIgnoreCase("BossShield"))
			.forEach(entity -> entity.remove());
		});
	}
	
	/*
	 ボスモンスタープラグイン 概要(仮値使用)
	・10秒位のトグルでプレイヤー&ボスの攻守交替
	・ボス攻撃タイムはプレイヤー一切攻撃不可能＆吹き飛ばしカウンター付き
	・プレイヤー攻撃タイム時にDPSを計算し、ヘイトを設定する
	・ボスの攻撃パターンは3種x3(パターン変更時の種類)位？
	・ボスが召喚されたときに射程圏内に入っていた場合、倒すまで一定範囲以上離れられない
	・残りHP80%、50%、20%の時に攻撃パターン変更(火力増加及び判定範囲拡大など)
	・プレイヤーが全滅した場合は、ボス自動消滅
	・制限時間に討伐できない場合は、広範囲即死攻撃
	・戦闘開始後は外からの干渉は不可能(強制的に触れようとした場合は即死)
	 */
	
	/*
	 * 石を持ち上げてくるくる回してからプレイヤーに飛ばす
	 * 
	 * 
	 * 
	 * 
	 */
}
