package jp.kotmw.together.test2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;
import jp.kotmw.together.util.WoolColorEnum;

public class Test2_Schedule extends BukkitRunnable {

	Location center;
	int phidegree;
	
	public Test2_Schedule(Location center) {
		this.center = center;
	}
	
	@Override
	public void run() {
		if(phidegree >= 360) 
			phidegree = 0;
		for(double theta = 0.0; theta <= 10.0; theta += 0.05) {
			Location center = this.center.clone();
			//Polar_coodinates pc = new Polar_coodinates(center.getWorld(),(theta / Math.PI), theta, Math.toRadians(30));
			Polar_coodinates pc = new Polar_coodinates(center.getWorld(),(Math.sin(2*theta))*4, theta, Math.toRadians(phidegree));
			//Polar_coodinates pc = new Polar_coodinates(center.getWorld(),3 , theta, Math.toRadians(phidegree));
			//Polar_coodinates pc = new Polar_coodinates(center.getWorld(),3*(1+Math.cos(theta)) , theta, Math.toRadians(30));
			WoolColorEnum color = WoolColorEnum.BLACK;
			center.add(pc.convertLocation());
			Bukkit.getOnlinePlayers().stream().filter(player -> pc.getWorld().getName().equals(player.getLocation().getWorld().getName()))
			.forEach(player -> new ParticleAPI.Particle(EnumParticle.REDSTONE, 
					center, 
					color.getRed(), 
					color.getGreen(), 
					color.getBlue(), 
					1, 
					0).sendParticle(player));
			
		}
		phidegree+=2;
	}
}
