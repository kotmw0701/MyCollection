package jp.kotmw.together.bossmonster;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.Main;
import jp.kotmw.together.bossmonster.skills.Instant_Death;
import jp.kotmw.together.bossmonster.skills.SkillBase;

public class Boss extends BukkitRunnable implements Listener{

	private final double maxbosshelth = 2000;
	private List<String> challengers = new ArrayList<>();
	private Map<String, Double> hate = new HashMap<>();
	private Map<String, Double> dps = new HashMap<>();
	private Creature boss;
	private SkillBase skill;
	private int tick = (20*60)*1+20*3;//20分？+3秒
	private int timecount;
	private int togglesecond = 10;
	private boolean playerturn = true;
	private boolean isstarted;
	public static final String BOSS_PREFIX = "[BossSystem"+(Main.bossdebug ? " - DebugMode" : "")+"] ";	
	
	public Boss(Location loc) {
		boss = (Creature) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
		boss.setAI(false);
		boss.setMaxHealth(maxbosshelth);
		boss.setHealth(maxbosshelth);
		Bukkit.getOnlinePlayers().stream().filter(player -> (boss.getLocation().distance(player.getLocation()) <= 20)).forEach(player -> challengers.add(player.getName()));
		sendChallengersMessage(ChatColor.GREEN+"概要: "+ChatColor.GOLD+"あなた達はボスの対戦者として設定されました、ボスを倒す、若しくは、死ぬまでここからは出られません");
		if(Main.bossdebug) {
			sendChallengersMessage("デバッグモードが有効になっています");
			challengers.forEach(player -> sendChallengersMessage(player));
		}
	}
	
	public void start() {
		if(challengers.isEmpty())
			throw new NullPointerException("参加者が存在していません");
		challengers.forEach(player -> hate.put(player, 0.0));
		this.isstarted = true;
		boss.setAI(true);
		sendChallengersMessage("戦闘開始");
		this.runTaskTimer(Main.instance, 0, 1);
	}
	
	public void kill() {
		boss.remove();
	}

	@Override
	public void run() {
		if(boss.isDead()) {
			this.cancel();
			sendChallengersMessage("討伐成功！");
			return;
		}
		if(tick > 20*3) {
			if(tick%20 == 0) {
				if(timecount%2 == 0) {
					Random random = new Random();
					if(skill != null && skill.isRunning()) {
						switch(random.nextInt(20)) {
						case 0:
							break;
						case 1:
							break;
						}
					}
				}
				if(timecount >= togglesecond) {
					timecount = 0;
					settingPlayerHate();
				}
				timecount++;
			}
		} else {
			if(tick%20 == 0) {
				if(tick/20 == 3) {
					sendChallengersMessage("時間切れの為、即死攻撃を実行し、戦闘を強制終了します");
					new Instant_Death(this);
					boss.remove();
					this.cancel();
				}
			}
		}
		tick--;
	}
	
	public void sendChallengersMessage(String msg) {
		challengers.forEach(player -> Bukkit.getPlayer(player).sendMessage(BOSS_PREFIX+msg));
	}
	
	public boolean isStarted() {
		return isstarted;
	}
	
	public boolean isPlayerturn() {
		return playerturn;
	}
	
	public boolean isChallenger(Player player) {
		return challengers.contains(player.getName());
	}
	
	public boolean isBoss(Entity entity) {
		return boss.getUniqueId().equals(entity.getUniqueId());
	}
	
	public List<Player> getchallengers() {
		List<Player> challengers = new ArrayList<>();
		this.challengers.forEach(player -> challengers.add(Bukkit.getPlayer(player)));
		return challengers;
	}
	
	public Creature getBoss() {
		return boss;
	}
	
	public void addDamage(String player, double damage) {
		if(!dps.containsKey(player)) {
			dps.put(player, damage);
			return;
		}
		dps.put(player, dps.get(player)+damage);
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
}
