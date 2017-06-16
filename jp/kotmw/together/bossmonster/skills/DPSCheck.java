package jp.kotmw.together.bossmonster.skills;

import jp.kotmw.together.bossmonster.Boss;

public class DPSCheck extends SkillBase {

	public DPSCheck(Boss boss) {
		super(boss);
		runDelay(60*20);
	}

	@Override
	protected void fire() throws InterruptedException {
		
	}
}
