package jp.kotmw.together.test3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.test2.Polar_coodinates;
import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.DetailsColor.DetailsColorType;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;
import net.minecraft.server.v1_10_R1.AxisAlignedBB;

public class Turret extends BukkitRunnable {

	private DetailsColor color = new DetailsColor("#1a0963");
	private DetailsColor color2 = DetailsColorType.WoolColor_ORANGE.getColor();
	ArmorStand turret;
	
	public Turret(ArmorStand stand) {
		turret = stand;
		turret.setCustomName("Turret");
		turret.setCustomNameVisible(true);
	}
	
	@Override
	public void run() {
		if(turret.isDead()) {
			cancel();
			return;
		}
		for(double theta = 0.0; theta <= 2*Math.PI; theta += Math.PI/120) {
			setParticle(turret.getLocation().add(new Polar_coodinates(turret.getWorld(), 20, theta, 0).convertLocation()), color2);
		}
		Bukkit.getOnlinePlayers().stream()
		.filter(player -> player.getWorld().getName().equals(turret.getWorld().getName()))
		.filter(player -> player.getLocation().distance(turret.getLocation()) <= 20)
		.filter(player -> !a(player))
		.forEach(player -> fire(player));
	}
	
	private boolean a(Player param1) {
		Location loc = turret.getEyeLocation(), loc2 = param1.getEyeLocation();
		double theta = Math.atan2(loc2.getX()-loc.getX(), loc2.getZ()-loc.getZ());
		for(double radius = 0.0 ; radius <= loc2.distance(loc); radius += 0.1) {
			Polar_coodinates pc = new Polar_coodinates(loc.getWorld(), radius, theta, 0);
			Location newloc = loc.clone().add(pc.convertLocation());
			if(newloc.getBlock().getType() != Material.AIR) return true;
		}
		return false;
	}
	
	private void fire(Player param1) {
		Location loc = turret.getEyeLocation(), loc2 = param1.getEyeLocation();
		double theta = Math.atan2(loc2.getX()-loc.getX(), loc2.getZ()-loc.getZ());
		for(double radius = 0.0 ; radius <= loc2.distance(loc); radius += 0.2) {
			Polar_coodinates pc = new Polar_coodinates(loc.getWorld(), radius, theta, 0);
			Location particleloc = loc.clone().add(pc.convertLocation());
			setDamageParticle(particleloc, 20, param1);
			setParticle(particleloc, color);
		}
	}
	
	private void setParticle(Location particleloc, DetailsColor color) {
		Bukkit.getOnlinePlayers().stream()
		.filter(player -> player.getWorld().getUID().equals(turret.getWorld().getUID()))
		.forEach(player -> {
			new ParticleAPI.Particle(EnumParticle.REDSTONE, 
					particleloc, 
					color.getRed(), 
					color.getGreen(), 
					color.getBlue(), 
					1, 
					0).sendParticle(player);
		});
	}
	
	private void setDamageParticle(Location particleloc, double damage, Entity entity) {
		AxisAlignedBB aabb = new AxisAlignedBB(particleloc.getX()+0.05, particleloc.getY()-0.05, particleloc.getZ()-0.05, particleloc.getX()+0.05, particleloc.getY()+0.05, particleloc.getZ()+0.05);
		Location entityloc = entity.getLocation();
		boolean a = aabb.b(new AxisAlignedBB(entityloc.getX()-0.4, entityloc.getY()-0.0, entityloc.getZ()-0.4, entityloc.getX()+0.4, entityloc.getY()+1.8, entityloc.getZ()+0.4));
		if(!a)
			return;
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		LivingEntity livingentity = (LivingEntity)entity;
		livingentity.damage(damage);
	}
}
