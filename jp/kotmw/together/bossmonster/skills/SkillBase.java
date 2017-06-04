package jp.kotmw.together.bossmonster.skills;

import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import jp.kotmw.together.Main;
import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.bossmonster.BossAttackRange;
import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;
import net.minecraft.server.v1_10_R1.AxisAlignedBB;

public abstract class SkillBase extends Thread {

	protected Boss boss;
	private boolean running = true;
	private int delaytick, count;
	private BossAttackRange bsr;
	
	public SkillBase(Boss boss) {
		this.boss = boss;
		boss.getBoss().teleport(boss.getBoss().getLocation());
	}
	
	@Override
	public void run() {
		try {
			while(delaytick+10 >= count) {
				sleep(50);
				if(delaytick == count) 
					if(bsr != null)
						bsr.cancel();
				count++;
			}
			if(getBoss().isDead() || !running) {
				cancel();
				return;
			}
			fire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected Creature getBoss() {
		return boss.getBoss();
	}
	
	protected void setBSR(BossAttackRange bsr) {
		this.bsr = bsr;
	}
	
	protected void syncDamage(double damage, Entity entity) {
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		LivingEntity livingentity = (LivingEntity)entity;
		(new BukkitRunnable() {
			@Override
			public void run() {
				livingentity.damage(boss.getDiffDamage(damage), boss.getBoss());
			}
		}).runTask(Main.instance);
	}
	
	protected void sendReddust(Location center, DetailsColor color) {
		sendParticle(EnumParticle.REDSTONE, center, color.getRed(), color.getGreen(), color.getBlue(), false, 0.0);
	}
	protected void sendReddust(Location center, DetailsColor color, boolean damage, double damageparam) {
		sendParticle(EnumParticle.REDSTONE, center, color.getRed(), color.getGreen(), color.getBlue(), damage, damageparam);
	}
	
	protected void sendParticle(EnumParticle type, Location center, float offsetX, float offsetY, float offsetZ) {
		sendParticle(type, center, offsetX, offsetY, offsetZ, false, 0.0);
	}
	
	protected void sendParticle(EnumParticle param1, Location param2, float param3, float param4, float param5, boolean param6, double param7) {
		boss.getchallengers().stream().filter(player -> param2.getWorld().getName().equals(player.getLocation().getWorld().getName()))
		.forEach(player -> {
			if(param6) setDamageParticle(param2, param7, player);
			new ParticleAPI.Particle(param1, 
					param2, 
					param3, 
					param4, 
					param5, 
					1, 
					0).sendParticle(player);
		});
	}
	
	protected void setDamageParticle(Location particleloc, double damage, Entity entity) {
		AxisAlignedBB aabb = new AxisAlignedBB(particleloc.getX()+0.05, particleloc.getY()-0.05, particleloc.getZ()-0.05, particleloc.getX()+0.05, particleloc.getY()+0.05, particleloc.getZ()+0.05);
		Location entityloc = entity.getLocation();
		boolean a = aabb.a(entityloc.getX()-0.4, entityloc.getY()-0.0, entityloc.getZ()-0.4, entityloc.getX()+0.4, entityloc.getY()+1.8, entityloc.getZ()+0.4);
		if(a) syncDamage(damage, entity);
	}
	protected void resetLocation() {
		getBoss().teleport(boss.getCenterLocation());
		getBoss().setVelocity(new Vector(0, 0, 0));
	}
	
	public void runDelay(int delay) {
		this.delaytick = delay;
		start();
	}
	
	public void cancel() {
		running = false;
		boss.setPlayerTurn(true);
		boss.getBoss().setAI(true);
		boss.resetPriorityChallenger();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	abstract protected void fire() throws InterruptedException;
}
