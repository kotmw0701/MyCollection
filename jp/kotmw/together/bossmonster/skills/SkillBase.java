package jp.kotmw.together.bossmonster.skills;

import org.bukkit.entity.Creature;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.Main;
import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.bossmonster.BossAttackRange_Circle;

public abstract class SkillBase extends BukkitRunnable {

	protected Boss boss;
	private boolean running = true;
	private BossAttackRange_Circle bsr;
	
	public SkillBase(Boss boss) {
		this.boss = boss;
		boss.getBoss().setAI(false);
	}
	
	@Override
	public void run() {
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(bsr != null)
					bsr.cancel();
				if(getBoss().isDead()) {
					cencel();
					return;
				}
				fire();
			}
		};
		runnable.runTaskLater(Main.instance, 10);
	}
	
	protected Creature getBoss() {
		return boss.getBoss();
	}
	
	protected void setBSR(BossAttackRange_Circle bsr) {
		this.bsr = bsr;
	}
	
	public void cencel() {
		super.cancel();
		running = false;
		boss.getBoss().setAI(true);
	}
	
	public boolean isRunning() {
		return running;
	}
	
	abstract protected boolean fire();
}
