package jp.kotmw.together.bossmonster;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import jp.kotmw.together.test2.Polar_coodinates;

public class Shield extends BukkitRunnable {

	private Boss boss;
	private double theta;
	private boolean run = false;
	private List<ArmorStand> stands = new ArrayList<>();
	
	/*
	 * 大きい時は
	 * 270 270 0
	 * 
	 * 小さいときは
	 * 0 250 270
	 */
	
	public Shield(Boss boss, int count) {
		this.boss = boss;
		Polar_coodinates pc = new Polar_coodinates(boss.getBoss().getWorld(), 0.7, 0, 0);
		for(double num = 0.0; num < 2*Math.PI; num+=2*Math.PI/count) {
			pc.setTheta(num);
			ArmorStand stand = (ArmorStand) boss.getBoss().getWorld().spawnEntity(new Location(boss.getBoss().getWorld(), 0, 0.5, 0, (float)-Math.toDegrees(num), 0).add(pc.convertLocation()).add(boss.getBoss().getLocation()), EntityType.ARMOR_STAND);
			stand.getEquipment().setItemInMainHand(new ItemStack(Material.SHIELD));
			stand.setCustomName("BossShield");
			stand.setGravity(false);
			stand.setVisible(false);
			stand.setSmall(true);
			stand.setRightArmPose(new EulerAngle(0, Math.toRadians(250), 3*Math.PI/2));
			stands.add(stand);
		}
	}
	
	@Override
	public void run() {
		if(run == false)
			run = true;
		if(theta >= 2*Math.PI)
			theta = 0;
		Polar_coodinates pc;
		int i = 0;
		for(ArmorStand stand : stands) {
			pc = new Polar_coodinates(stand.getWorld(), 0.7, theta+((2*Math.PI/stands.size())*i), 0);
			stand.teleport(new Location(stand.getWorld(), 0, 0, 0, (float)-Math.toDegrees(theta+((2*Math.PI/stands.size())*i)), 0).add(boss.getBoss().getLocation().add(pc.convertLocation())));
			i++;
		}
		theta += Math.PI/120;
	}
	
	public List<ArmorStand> getShieldStands() {
		return stands;
	}
	
	public boolean isRunning() { 
		return run;
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		Bukkit.getScheduler().cancelTask(getTaskId());
		stands.forEach(stand -> stand.remove());
		run = false;
	}
}
