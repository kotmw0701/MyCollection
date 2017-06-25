package jp.kotmw.together.test2;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.DetailsColor.DetailsColorType;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;

public class Test2_Schedule extends BukkitRunnable {

	Location center;
	double phirad;
	double rotate;
	DetailsColor color = new DetailsColor("#000000");
	DetailsColor color2 = new DetailsColor("#ffffff");
	DetailsColor color3 = DetailsColorType.WoolColor_RED.getColor();
	
	public Test2_Schedule(Location center) {
		this.center = center;
	}
	
	@Override
	public void run() {
		if(phirad >= 2*Math.PI) 
			phirad = 0;
		if(rotate >= 2*Math.PI)
			rotate = 0;
		for(double theta = 0; theta <= 2*Math.PI; theta+=Math.PI/90) {
			Polar_coodinates pc = new Polar_coodinates(center.getWorld(), 5, theta, Math.toRadians(30));
			//Polar_coodinates pc2 = new Polar_coodinates(center.getWorld(), 5, theta, theta);
			Bukkit.getOnlinePlayers().stream().filter(player -> center.getWorld().getName().equals(player.getLocation().getWorld().getName()))
			.forEach(player -> {
				new ParticleAPI.Particle(EnumParticle.REDSTONE, 
					center.clone().add(pc.rotation_Yaxis(phirad)), 
					color.getRed(), 
					color.getGreen(), 
					color.getBlue(), 
					1, 
					0).sendParticle(player);
			});
		}
		phirad+=Math.PI/90;
		rotate+=Math.PI/90;
	}
}
