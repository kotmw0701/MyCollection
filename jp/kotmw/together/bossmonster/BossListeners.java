package jp.kotmw.together.bossmonster;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import jp.kotmw.together.Main;

public class BossListeners implements Listener{
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(Main.instance.boss == null || Main.instance.boss.getBoss().isDead())
			return;
		if(!Main.instance.boss.isBoss(e.getEntity()))
			return;
		if(!(e.getDamager().getType() == EntityType.PLAYER)) {
			e.setCancelled(true);
			return;
		}
		Player player = (Player)e.getDamager();
		if(!Main.instance.boss.isChallenger(player)) {
			player.sendMessage(Boss.BOSS_PREFIX+ChatColor.RED+"参加者以外のプレイヤーは戦闘への干渉を禁止されています");
			e.setCancelled(true);
			return;
		}
		if(!Main.instance.boss.isStarted()) {
			player.sendMessage(Boss.BOSS_PREFIX+ChatColor.RED+"戦闘が開始されてないので攻撃は不可能です");
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
			Vector vec = new Vector(-1*player.getLocation().getDirection().getX(), -0.8*player.getLocation().getDirection().getY(), -1*player.getLocation().getDirection().getZ());
			player.setVelocity(vec);
			player.damage(3);
			e.setCancelled(true);
			return;
		}
		if(!Main.instance.boss.isPlayerturn()) {
			player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
			Vector vec = new Vector(-1*player.getLocation().getDirection().getX(), -0.8*player.getLocation().getDirection().getY(), -1*player.getLocation().getDirection().getZ());
			player.setVelocity(vec);
			player.damage(3);
			e.setCancelled(true);
			return;
		}
		e.setDamage(e.getDamage()/5);
		Main.instance.boss.addDamage(player.getName(), (e.getDamage()/5));
	}
}
