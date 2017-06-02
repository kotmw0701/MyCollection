package jp.kotmw.together.bossmonster.skills;

import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.bossmonster.BossAttackRange_Circle;

public class Instant_Death extends SkillBase {

	BossAttackRange_Circle bsr;
	
	public Instant_Death(Boss boss) {
		super(boss);
		boss.getBoss().teleport(boss.getCenterLocation());
		boss.sendChallengersMessage("(なんか喋る(適当)(オタク特有の早口)(俊足)(コーナーで差をつけろ))");
		bsr = new BossAttackRange_Circle(boss, boss.getBoss().getLocation(), 20, 10, 20);
		bsr.start();
		setBSR(bsr);
		runDelay(20*5);
	}
	

	@Override
	protected void fire() {
		boss.getchallengers().forEach(player -> syncDamage(2048, player));
		boss.getBoss().remove();
	}
}
