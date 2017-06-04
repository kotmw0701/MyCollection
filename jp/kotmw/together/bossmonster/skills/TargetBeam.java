package jp.kotmw.together.bossmonster.skills;

import org.bukkit.Location;

import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.bossmonster.BossAttackRange;
import jp.kotmw.together.bossmonster.BossAttackRange_Rectangle;
import jp.kotmw.together.test2.Polar_coodinates;
import jp.kotmw.together.util.DetailsColor;

public class TargetBeam extends SkillBase {

	BossAttackRange bsr;
	private Location loc = boss.getBoss().getEyeLocation(), loc2;
	
	DetailsColor color = new DetailsColor("#2b0060");
	double theta;
	
	public TargetBeam(Boss boss) {
		super(boss);
		boss.getBoss().setAI(false);
		boss.getBoss().setTarget(null);
		loc2 = boss.getHatePlayer(1).getLocation();
		theta = Math.atan2(loc2.getX()-loc.getX(), loc2.getZ()-loc.getZ());
		bsr = new BossAttackRange_Rectangle(boss, boss.getBoss().getLocation(), 30, 4, theta);
		bsr.start();
		setBSR(bsr);
		runDelay(20*3);
	}

	@Override
	protected void fire() throws InterruptedException {
		for(double radius = 0.0; radius <= 30; radius+=0.1) {
			Polar_coodinates pc = new Polar_coodinates(boss.getBoss().getLocation().getWorld(), radius, theta, 0);
			sendReddust(loc.clone().add(pc.convertLocation()).add(0, -0.5, 0), color, true, 20);
		}
		cancel();
	}
}
