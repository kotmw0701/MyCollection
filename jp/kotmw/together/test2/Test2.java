package jp.kotmw.together.test2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.Main;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;
import jp.kotmw.together.util.WoolColorEnum;

public class Test2 implements Listener {
	
	Map<String, BukkitRunnable> runnable = new HashMap<>();
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
		if(player.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM) {
			if(!runnable.containsKey(player.getName())) {
				Test2_Schedule runnable = new Test2_Schedule(player.getLocation());
				runnable.runTaskTimer(Main.instance, 0, 1);
				this.runnable.put(player.getName(), runnable);
			} else this.runnable.remove(player.getName()).cancel();
		} else if(player.getInventory().getItemInMainHand().getType() == Material.GOLD_HOE) {
			thunder(player.getEyeLocation());
		}
	}
	
	private void thunder(Location loc) {
		WoolColorEnum color = WoolColorEnum.YELLOW;
		Location center = loc.clone();
		double max = 2, yaw = -loc.getYaw(), deg = 0, deg2 = 0;
		int angle = 90;
		for(double i = 0.00; i <= max; i+=0.01) {
			Random random = new Random();
			if(random.nextInt(10) == 5) {
				max = max-i;
				deg = random.nextInt(angle)-(angle/2);
				deg2 = random.nextInt(angle)-(angle/2);
			}
			Polar_coodinates pc = new Polar_coodinates(center.getWorld(), i, Math.toRadians(yaw+deg), Math.toRadians(deg2));
			center.add(pc.convertLocation());
			Bukkit.getOnlinePlayers().stream().filter(player -> loc.getWorld().getName().equals(player.getLocation().getWorld().getName()))
			.forEach(player -> new ParticleAPI.Particle(EnumParticle.REDSTONE, 
					center, 
					color.getRed(), 
					color.getGreen(), 
					color.getBlue(), 
					1, 
					0).sendParticle(player));
		}
	}
}
