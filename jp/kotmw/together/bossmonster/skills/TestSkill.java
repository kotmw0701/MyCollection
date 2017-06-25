package jp.kotmw.together.bossmonster.skills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.Main;
import jp.kotmw.together.bossmonster.BossAttackRange;
import jp.kotmw.together.bossmonster.BossAttackRange_Circle;
import jp.kotmw.together.test2.Polar_coodinates;
import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;

public class TestSkill extends BukkitRunnable{

	Location center, groundtarget;
	double radius = 10.0, theta2 = 0.0;
	DetailsColor color = new DetailsColor("#1a0963");
	double countertick = (10*20);
	BossAttackRange bsr; 
	
	public TestSkill(Location center) {
		this.center = center.clone().add(0, 3, 0);
		this.bsr = new BossAttackRange_Circle(Main.instance.boss, groundtarget = getGroundLocation(), 20);
	}
	
	@Override
	public void run() {
		if(theta2 >= 2*Math.PI)
			theta2 = 0;
		if(countertick > 8*20) radius -= 0.2; 
		else if ((countertick == 6*20)) {
			bsr.start();
		} else if (countertick == 10) { //18tick
			for(double i = 1.0; i <= center.distance(groundtarget) ; i+=0.1) {
				double x = (i*Math.sin(Math.toRadians(0)));
				double y = (i*Math.sin(Math.toRadians(-30)));
				double z = (i*Math.cos(Math.toRadians(0)));
				sendParticle(new Location(center.getWorld(), x, y, z));
			}
		} else if (countertick < 0) {
			bsr.cancel();
			groundtarget.getWorld().createExplosion(groundtarget, 60, true);
			cancel();
		}
		for(double theta = 0.0; theta <= 2*Math.PI; theta += Math.PI/60.0) {
			Polar_coodinates pc = new Polar_coodinates(center.getWorld(), radius, theta, theta2);
			sendParticle(pc.rotation_Yaxis(theta2));
			pc.setPhi(-theta2);
			sendParticle(pc.rotation_Xaxis(-theta2));
			pc.setPhi(Math.toRadians(45));
			sendParticle(pc.rotation_Zaxis(-theta2));
		}
		theta2+=Math.PI/120.0;
		countertick--;
	}
	
	private void sendParticle(Location loc) {
		Bukkit.getOnlinePlayers().stream().filter(player -> center.getWorld().getName().equals(player.getLocation().getWorld().getName()))
		.forEach(player -> {
			new ParticleAPI.Particle(EnumParticle.REDSTONE, 
				center.clone().add(loc), 
				color.getRed(), 
				color.getGreen(), 
				color.getBlue(), 
				1, 
				0).sendParticle(player);
		});
	}
	
	private Location getGroundLocation() {
		double phi = -30;
		for(double i = 0.0; i <= 50; i += 0.1) {
			double x = center.getX()+(i*Math.sin(Math.toRadians(0)));
			double y = center.getY()+(i*Math.sin(Math.toRadians(phi)));
			double z = center.getZ()+(i*Math.cos(Math.toRadians(0)));
			Location loc = new Location(center.getWorld(), x, y+1, z);
			if((loc.getBlock().getType() != null) && (loc.getBlock().getType() != Material.AIR))
				return loc;
		}
		return center;
	}
}
