package jp.kotmw.together.bossmonster.skills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
	
	/**
	 * 
	 * @return スキルを発動する対象のボス
	 */
	protected Creature getBoss() {
		return boss.getBoss();
	}
	
	/**
	 * 
	 * @param bsr 予測線クラスのスーパークラスを返す
	 */
	protected void setBSR(BossAttackRange bsr) {
		this.bsr = bsr;
	}
	
	/**
	 * Bukkitのスレッドとは非同期のため、ダメージを非同期で与えて死んでしまうと、エラーが発生するために、同期処理を行う
	 * 
	 * @param damage ダメージ数(1.0でハート1個)
	 * @param entity 対象のEntity
	 */
	protected void syncDamage(double damage, Entity entity) {
		if(entity == null || !(entity instanceof LivingEntity))
			return;
		LivingEntity livingentity = (LivingEntity)entity;
		syncThread(() -> {
			livingentity.damage(boss.getDiffDamage(damage), boss.getBoss());
		});
	}
	
	/**
	 * 
	 * @param runnable 同期させたい処理をぶち込む(雑
	 */
	protected void syncThread(Runnable runnable) {
		Bukkit.getScheduler().runTask(Main.instance, runnable);
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
	
	protected void setBossStatus(boolean ai, boolean playerturn) {
		boss.getBoss().setAI(ai);
		boss.setPlayerTurn(playerturn);
	}
	
	protected void resetLocation() {
		getBoss().teleport(boss.getCenterLocation());
		getBoss().setVelocity(new Vector(0, 0, 0));
	}
	
	public void runDelay(int delay) {
		this.delaytick = delay;
		start();
	}
	
	public synchronized void cancel() {
		running = false;
		boss.setPlayerTurn(true);
		boss.getBoss().setAI(true);
	}
	
	public boolean isRunning() {
		return running;
	}
	
	abstract protected void fire() throws InterruptedException;
}
