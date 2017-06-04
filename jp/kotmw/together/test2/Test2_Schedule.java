package jp.kotmw.together.test2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;

public class Test2_Schedule extends BukkitRunnable {

	Location center;
	double radius;
	int phidegree;
	int thetadeg = -90;
	
	public Test2_Schedule(Location center) {
		this.center = center;
	}
	
	@Override
	public void run() {
		if(phidegree >= 360) 
			phidegree = 0;
		if(thetadeg >= 90)
			thetadeg = -90;
		if(radius >= 5)
			radius = 0;
		for(double theta = 0; theta <= 2*Math.PI; theta+=Math.PI/90) {
			//Polar_coodinates pc = new Polar_coodinates(center.getWorld(),(theta / Math.PI), theta, Math.toRadians(30));
			//Polar_coodinates pc = new Polar_coodinates(center.getWorld(),(Math.sin(2*theta))*4, theta, 0);
			Polar_coodinates pc = new Polar_coodinates(center.getWorld(), 5, theta, Math.toRadians(phidegree));
			//Polar_coodinates pc = new Polar_coodinates(center.getWorld(),3*(1+Math.cos(theta)) , theta, Math.toRadians(30));
			DetailsColor color = new DetailsColor("#ff6000");
			Bukkit.getOnlinePlayers().stream().filter(player -> pc.getWorld().getName().equals(player.getLocation().getWorld().getName()))
			.forEach(player -> {
				new ParticleAPI.Particle(EnumParticle.REDSTONE, 
					center.clone().add(pc.convertLocation()), 
					color.getRed(), 
					color.getGreen(), 
					color.getBlue(), 
					1, 
					0).sendParticle(player);
			});
			
		}
		phidegree+=2;
		thetadeg+=1;
		radius+=0.1;
	}
}
