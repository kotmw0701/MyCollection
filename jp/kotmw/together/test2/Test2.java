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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import jp.kotmw.together.Main;
import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.DetailsColor.DetailsColorType;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;
import net.minecraft.server.v1_10_R1.AxisAlignedBB;

public class Test2 implements Listener {
	
	public static double theta, phi;
	public static boolean circle;
	public static int x,y,z;
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
				TestClass2 runnable2 = new TestClass2(player.getLocation(), 20, 4);
				runnable2.start();
				this.runnable2.put(player.getName(), runnable2);
			} else this.runnable2.remove(player.getName()).cancel();
		} else if (player.getInventory().getItemInMainHand().getType() == Material.BOOK) {
			if(!player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("Boss"))
				return;
			player.teleport(new Location(player.getWorld(), 2081.5, 250.5, -751.5));
		} else if (player.getInventory().getItemInMainHand().getType() == Material.NETHER_STAR) { 
			if(!runnable2.containsKey(player.getName())) {
				TestClass3 runnable2 = new TestClass3(player.getLocation());
				runnable2.start();
				this.runnable2.put(player.getName(), runnable2);
			} else this.runnable2.remove(player.getName()).cancel();
		} else if (player.getInventory().getItemInMainHand().getType() == Material.SHIELD) {
			ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
			stand.setArms(true);
			stand.setSmall(true);
			stand.getEquipment().setItemInMainHand(new ItemStack(Material.SHIELD));
			stand.setRightArmPose(new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
		} else if (player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
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
				TestClass2 thread = new TestClass2(entity.getLocation(), 30, 2);
				thread.start();
				BukkitRunnable runnable = new BukkitRunnable() {
					@Override
					public void run() {
						thread.cancel();
						BukkitRunnable runnable2 = new BukkitRunnable() {
							@Override
							public void run() {
								for(double radius = 0.0; radius <= 30; radius+=0.1) {
									Polar_coodinates pc = new Polar_coodinates(player.getLocation().getWorld(), radius, Math.toRadians(-entity.getLocation().getYaw()), 0);
									sendReddust(entity.getEyeLocation().clone().add(pc.convertLocation()).add(0, -0.5, 0), new DetailsColor("#2b0060"), true, 20);
								}
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
	
	//////////////////////////////////////////////////////////////////////////////
	protected void sendReddust(Location center, DetailsColor color, boolean damage, double damageparam) {
		sendParticle(EnumParticle.REDSTONE, center, color.getRed(), color.getGreen(), color.getBlue(), damage, damageparam);
	}
	
	protected void sendParticle(EnumParticle param1, Location param2, float param3, float param4, float param5, boolean param6, double param7) {
		Bukkit.getOnlinePlayers().stream().filter(player -> param2.getWorld().getName().equals(player.getLocation().getWorld().getName()))
		.forEach(player -> {
			if(param6) setDamageParticle(param2, param7);
			new ParticleAPI.Particle(param1, 
					param2, 
					param3, 
					param4, 
					param5, 
					1, 
					0).sendParticle(player);
		});
	}
	
	protected void setDamageParticle(Location particleloc, double damage) {
		AxisAlignedBB aabb = new AxisAlignedBB(particleloc.getX()+0.05, particleloc.getY()-0.05, particleloc.getZ()-0.05, particleloc.getX()+0.05, particleloc.getY()+0.05, particleloc.getZ()+0.05);
		particleloc.getWorld().getEntities().forEach(entity -> {
			Location entityloc = entity.getLocation();
			boolean a = aabb.a(entityloc.getX()-0.4, entityloc.getY()-0.0, entityloc.getZ()-0.4, entityloc.getX()+0.4, entityloc.getY()+1.8, entityloc.getZ()+0.4);
			if(a) syncDamage(damage, entity);
		});
	}
	
	protected void syncDamage(double damage, Entity entity) {
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		LivingEntity livingentity = (LivingEntity)entity;
		(new BukkitRunnable() {
			@Override
			public void run() {
				double damage2 = damage;
				switch(entity.getWorld().getDifficulty()) {
				case EASY:
					damage2 *= 2;
					break;
				case HARD:
					damage2 *= 0.5;
					break;
				case NORMAL:
					break;
				default:
					return;
				}
				livingentity.damage(damage2);
			}
		}).runTask(Main.instance);
	}
	//////////////////////////////////////////////////////////////////////////////
	
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
					sleep(10);
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
		private int maxradius;
		private int maxwidth;
		private DetailsColor color = new DetailsColor("#ff7000");
		private DetailsColor color2 = new DetailsColor("#ff3000");
		
		public TestClass2(Location corner, int radius, int width) {
			this.corner = corner;
			this.maxradius = radius*5;
			this.maxwidth = width*5;
		}
		
		@Override
		public void run() {
			while(run) {
				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(move >= maxradius*2)
					move = 0;
				for(int radius = 0; radius <= maxradius; radius+=2) {
					for(int width = -(maxwidth/2); width <= (maxwidth/2); width+=2) {
						if((radius != 0 && radius != maxradius) && (width != -(maxwidth/2) && width != (maxwidth/2)))
							continue;
						Location loc = corner.clone()
								.add(new Polar_coodinates(corner.getWorld(), radius*0.2, Math.toRadians(-corner.getYaw()), 0).convertLocation())
								.add(new Polar_coodinates(corner.getWorld(), width*0.2, Math.toRadians(-(corner.getYaw()-90)), 0).convertLocation());
						Main.sendPlayersParticle(EnumParticle.REDSTONE, 
								loc, 
								color2.getRed(), 
								color2.getGreen(), 
								color2.getBlue(),
								Bukkit.getOnlinePlayers());
					}
				}
				for(int width = -(maxwidth/2)*2; width <= (maxwidth/2)*2; width+=2) {
					Location loc = corner.clone()
							.add(new Polar_coodinates(corner.getWorld(), move*0.1, Math.toRadians(-corner.getYaw()), 0).convertLocation())
							.add(new Polar_coodinates(corner.getWorld(), width*0.1, Math.toRadians(-(corner.getYaw()-90)), 0).convertLocation());
					Main.sendPlayersParticle(EnumParticle.REDSTONE, 
							loc, 
							color.getRed(), 
							color.getGreen(), 
							color.getBlue(),
							Bukkit.getOnlinePlayers());
				}
				move+=1;
			}
		}
	}
	
	private class TestClass3 extends ThreadBase {
		
		private Location center;
		private int expantiontick = 5;
		
		public TestClass3(Location center) {
			this.center = center;
		}
		
		@Override
		public void run() {
			try {
				Expansion();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void Expansion() throws InterruptedException {
			for(double theta = 0.0; theta <= 2*Math.PI; theta += Math.PI/360) {
				for(double y = 0; y <= 5; y+=0.5) {
					Polar_coodinates pc = new Polar_coodinates(center.getWorld(), 20, theta, 0);
					Main.sendPlayersParticle(EnumParticle.FLAME, 
							center.clone().add(pc.convertLocation()).add(0, y, 0), 
							0, 0, 0,
							Bukkit.getOnlinePlayers());
					pc = new Polar_coodinates(center.getWorld(), 20, -theta, 0);
					Main.sendPlayersParticle(EnumParticle.FLAME, 
							center.clone().add(pc.convertLocation()).add(0, y, 0), 
							0, 0, 0,
							Bukkit.getOnlinePlayers());
					
				}
				sleep(expantiontick);
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
