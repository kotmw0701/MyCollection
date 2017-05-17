package jp.kotmw.together.getvisibleplayer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Test1 implements Listener {

	Map<String, BukkitRunnable> runnable = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
		if(player.getInventory().getItemInMainHand().getType() != Material.MAGMA_CREAM) return;
		
		/*if(!runnable.containsKey(player.getName())) {
			Test1_Schedule runnable = new Test1_Schedule(player);
			runnable.runTaskTimer(Main.instance, 0, 1);
			this.runnable.put(player.getName(), runnable);
		} else this.runnable.remove(player.getName()).cancel();*/
	}
}
