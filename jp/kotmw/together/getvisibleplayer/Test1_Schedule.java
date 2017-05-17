package jp.kotmw.together.getvisibleplayer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.util.Title;

public class Test1_Schedule extends BukkitRunnable {

	private Player player;
	
	public Test1_Schedule(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		Location l = player.getLocation();
		@SuppressWarnings("unused")
		float yaw = Math.abs(l.getYaw()),pitch = Math.abs(l.getPitch());
		double xo = l.getX(), yo = l.getY(), zo = l.getZ();
		double pInterval = 0.5, max = 16, maxangle = 100;
		List<Entity> sightentity = new ArrayList<>();
		range: for(double range = 0.0; range <= maxangle; range += 1) {
			for(double i = pInterval; i <= max; i = i + pInterval) {
				double x = xo+(i*Math.sin(Math.toRadians((yaw-maxangle/2)+range)));
				double y = yo/*+(i*Math.sin(Math.toRadians(pitch)))*/;
				double z = zo+(i*Math.cos(Math.toRadians((yaw-maxangle/2)+range)));
				if(LocConversion(new Location(l.getWorld(), x, y, z)).getBlock().getType() != Material.AIR)
					continue range;
				Location center = LocConversion(new Location(l.getWorld(), x, y, z)).add(0.5, 0, 0.5);
				l.getWorld().getNearbyEntities(l, max, 1, max).forEach(entity -> {
					if(entity.getLocation().getBlockX() == center.getBlockX()
							&& entity.getLocation().getBlockY() == center.getBlockY()
							&& entity.getLocation().getBlockZ() == center.getBlockZ()) {
						sightentity.add(entity);
					}
				});
				Title.sendSubTitle(player, 0, 1, 0, ChatColor.GREEN.toString() + sightentity.size());
				sightentity.clear();
				/*WoolColorEnum color = WoolColorEnum.WHITE;
				if(range == 0.0 || range == maxrange) {
					center = new Location(l.getWorld(), x, y, z);
					color = WoolColorEnum.RED;
				}
				new Particle(EnumParticle.REDSTONE, 
						center, 
						color.getRed(), 
						color.getGreen(), 
						color.getBlue(), 
						1, 
						0).sendParticle(player);*/
			}
		}
	}
	
	public Location LocConversion(Location l) {
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
}
