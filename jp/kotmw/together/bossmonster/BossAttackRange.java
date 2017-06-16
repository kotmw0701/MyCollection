package jp.kotmw.together.bossmonster;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;

public abstract class BossAttackRange extends Thread {
	
	protected Boss boss;
	protected boolean cancel = false;
	protected List<Location> centers = new ArrayList<>();
	
	public BossAttackRange(Boss boss, Location center) {
		this.boss = boss;
		this.centers.add(center);
	}
	
	public BossAttackRange(Boss boss, List<Location> centers) {
		this.boss = boss;
		this.centers.addAll(centers);
	}
	
	@Override
	public abstract void run();
	
	public void setAttackRange(Location loc, DetailsColor color) {
		boss.getchallengers().stream().filter(player -> player.getWorld().getName().equals(loc.getWorld().getName())).forEach(player ->
			new ParticleAPI.Particle(EnumParticle.REDSTONE, 
					loc, 
					color.getRed(), 
					color.getGreen(), 
					color.getBlue(), 
					1, 
					0).sendParticle(player));
	}
	
	public void cancel() {
		this.cancel = true;
	}
	
	public Boss getBoss() {
		return boss;
	}
	
	public boolean isCancelled() {
		return cancel;
	}
}
