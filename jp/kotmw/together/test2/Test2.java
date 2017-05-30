package jp.kotmw.together.test2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.Main;
import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.DetailsColor.DetailsColorType;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;
import net.minecraft.server.v1_10_R1.AxisAlignedBB;

public class Test2 implements Listener {
	
	Map<String, BukkitRunnable> runnable = new HashMap<>();
	Map<String, ThreadBase> runnable2 = new HashMap<>();
	boolean running = false;
	int count;
	
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
		} else if(player.getInventory().getItemInMainHand().getType() == Material.STONE_HOE) { 
			beam(player.getEyeLocation());
		} else if(player.getInventory().getItemInMainHand().getType() == Material.GOLD_NUGGET) {
			if(!runnable2.containsKey(player.getName())) {
				TestClass runnable2 = new TestClass(player.getLocation(), 10);
				runnable2.start();
				this.runnable2.put(player.getName(), runnable2);
			} else this.runnable2.remove(player.getName()).cancel();
		} else if(player.getInventory().getItemInMainHand().getType() == Material.CARROT_STICK){
			ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			stand.setCustomName("Target");
			stand.setCustomNameVisible(true);
		} else if(player.getInventory().getItemInMainHand().getType() == Material.FEATHER){
			if(!runnable2.containsKey(player.getName())) {
				TestClass2 runnable2 = new TestClass2(player.getLocation());
				runnable2.start();
				this.runnable2.put(player.getName(), runnable2);
			} else this.runnable2.remove(player.getName()).cancel();
		} else if(player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
			if(running)
				return;
			running = true;
			count = 0;
			player.getWorld().getLivingEntities().stream()
			.filter(entity -> entity.getType().equals(EntityType.ARMOR_STAND))
			.filter(entity -> entity.getCustomName().equalsIgnoreCase("Target"))
			.forEach(entity -> {
				if(count >= 10)
					return;
				count++;
				TestClass thread = new TestClass(entity.getLocation(), 5);
				thread.start();
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {
						thread.cancel();
						BukkitRunnable runnable2 = new BukkitRunnable() {
							@Override
							public void run() {
								entity.getWorld().createExplosion(entity.getLocation().getX(), 
										entity.getLocation().getY(), 
										entity.getLocation().getZ(), 
										5, false, false);
								running = false;
							}
						};
						runnable2.runTaskLater(Main.instance, 10);
					}
				};
				runnable.runTaskLater(Main.instance, 5*20);
			});
		}
	}
	
	private void thunder(Location loc) {
		DetailsColor color = DetailsColorType.WoolColor_ORANGE.getColor();
		Location center = loc.clone();
		double max = 2, yaw = -loc.getYaw(), deg = 0, deg2 = 0;
		int angle = 90;
		for(double i = 0.00; i <= max; i+=0.01) {
			Random random = new Random();
			if(random.nextInt(10) == 5) {
				deg = random.nextInt(angle)-(angle/2);
				deg2 = random.nextInt(angle)-(angle/2);
			}
			Polar_coodinates pc = new Polar_coodinates(center.getWorld(), i, Math.toRadians(yaw+deg), Math.toRadians(deg2));
			center.add(pc.convertLocation());
			Bukkit.getOnlinePlayers().stream().filter(player -> loc.getWorld().getName().equals(player.getLocation().getWorld().getName()))
			.forEach(player -> {
				new ParticleAPI.Particle(EnumParticle.REDSTONE, 
					center, 
					color.getRed(), 
					color.getGreen(), 
					color.getBlue(), 
					1, 
					0).sendParticle(player);
			});
		}
	}
	
	private void beam(Location loc) {
		DetailsColor color = DetailsColorType.WoolColor_BLUE.getColor();
		float yaw = -loc.getYaw(),pitch = -loc.getPitch();
		for(double i = 0.1; i <= 10; i = i + 0.1) {
			double x = loc.getX()+(i*Math.sin(Math.toRadians(yaw)));
			double y = loc.getY()+(i*Math.sin(Math.toRadians(pitch)));
			double z = loc.getZ()+(i*Math.cos(Math.toRadians(yaw)));
			Main.sendPlayersParticle(EnumParticle.REDSTONE,
					new Location(loc.getWorld(), x, y, z), 
					color.getRed(), 
					color.getGreen(),
					color.getBlue(), 
					Bukkit.getOnlinePlayers());
		}
	}
	
	public static void damageparticle(Location particleloc, Entity entity) {
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		LivingEntity livingentity = (LivingEntity)entity;
		AxisAlignedBB aabb = new AxisAlignedBB(particleloc.getX()+0.01, particleloc.getY()-0.01, particleloc.getZ()-0.01, particleloc.getX()+0.01, particleloc.getY()+0.01, particleloc.getZ()+0.01);
		Location entityloc = entity.getLocation();
		boolean a = aabb.a(entityloc.getX()-0.4, entityloc.getY()-0.0, entityloc.getZ()-0.4, entityloc.getX()+0.4, entityloc.getY()+1.7, entityloc.getZ()+0.4);
		if(a) {
			BukkitRunnable runnable = new BukkitRunnable() {
				@Override
				public void run() {
					livingentity.damage(10.0);
				}
			};
			runnable.runTask(Main.instance);
		}
	}
	
	private class TestClass extends ThreadBase {
		
		private Location center;
		private double radius;
		private double radius_;
		private DetailsColor color = new DetailsColor("#ff5000");
		private DetailsColor color2 = new DetailsColor("#ff3000");
		
		public TestClass(Location center, double radius) {
			this.center = center;
			this.radius = radius;
		}
		
		@Override
		public void run() {
			try {
				for(double radius = 0.0; radius <= this.radius; radius+=0.1) {
					for(double theta = 0.0; theta <= 2*Math.PI; theta+=Math.PI/90) {
						Main.sendPlayersParticle(EnumParticle.REDSTONE, 
								center.clone().add(new Polar_coodinates(center.getWorld(), radius, theta, 0).convertLocation()), 
								color2.getRed(), 
								color2.getGreen(), 
								color2.getBlue(),
								Bukkit.getOnlinePlayers());
					}
					Thread.sleep(10);
				}
				while(run) {
					if(radius_ >= radius) {
						radius_ = 0;
					}
					Thread.sleep(50);
					for(double theta = 0.0; theta <= 2*Math.PI; theta+=Math.PI/90) {
						Main.sendPlayersParticle(EnumParticle.REDSTONE, 
								center.clone().add(new Polar_coodinates(center.getWorld(), radius_, theta, 0).convertLocation()), 
								color.getRed(), 
								color.getGreen(), 
								color.getBlue(),
								Bukkit.getOnlinePlayers());
						Main.sendPlayersParticle(EnumParticle.REDSTONE, 
								center.clone().add(new Polar_coodinates(center.getWorld(), radius, theta, 0).convertLocation()), 
								color2.getRed(), 
								color2.getGreen(), 
								color2.getBlue(),
								Bukkit.getOnlinePlayers());
					}
					radius_+=0.2;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class TestClass2 extends ThreadBase {

		private Location corner;
		private int move;
		private DetailsColor color = new DetailsColor("#ff5000");
		private DetailsColor color2 = new DetailsColor("#ff3000");
		
		public TestClass2(Location corner) {
			this.corner = corner;
		}
		
		@Override
		public void run() {
			while(run) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(move >= 100)
					move = 0;
				for(int x = 0; x <= 30; x+=2) {
					for(int z = 0; z <= 100; z+=2) {
						if((x==0.0 || z==0.0)||(x==30||z==100))
							Main.sendPlayersParticle(EnumParticle.REDSTONE, 
									corner.clone().add(x*0.2,0,z*0.2), 
									color2.getRed(), 
									color2.getGreen(), 
									color2.getBlue(),
									Bukkit.getOnlinePlayers());
					}
				}
				for(int movewidth = 0; movewidth <= 60; movewidth++) {
					Location corner2 = corner.clone().add(movewidth*0.1,0,move*0.2);
					corner.getWorld().getEntities().forEach(entity -> damageparticle(corner2, entity));
					Main.sendPlayersParticle(EnumParticle.REDSTONE, 
							corner2, 
							color.getRed(), 
							color.getGreen(), 
							color.getBlue(),
							Bukkit.getOnlinePlayers());
				}
				move+=1;
			}
		}
	}
	
	private abstract class ThreadBase extends Thread {
		
		protected boolean run = true;
		
		@Override
		public abstract void run();
		
		public void cancel() {
			run = false;
		}
	}
}
