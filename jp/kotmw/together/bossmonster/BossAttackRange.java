package jp.kotmw.together.bossmonster;

import org.bukkit.Location;

import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;

public abstract class BossAttackRange extends Thread {
	
	protected Boss boss;
	protected boolean cancel = false;
	
	public BossAttackRange(Boss boss) {
		this.boss = boss;
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
