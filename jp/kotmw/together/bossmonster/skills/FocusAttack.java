package jp.kotmw.together.bossmonster.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.bossmonster.BossAttackRange_Circle;

public class FocusAttack extends SkillBase {

	List<Location> locs = new ArrayList<>();
	BossAttackRange_Circle bsr;
	
	public FocusAttack(Boss boss) {
		super(boss);
		boss.getchallengers().forEach(player -> locs.add(player.getLocation()));
		bsr = new BossAttackRange_Circle(boss, locs, 5, 10, 50);
		bsr.start();
		setBSR(bsr);
		runDelay(20*5);
	}

	@Override
	protected void fire() throws InterruptedException {
		locs.forEach(loc -> loc.getWorld().createExplosion(loc.getX(), 
				loc.getY(), 
				loc.getZ(), 
				2.5f, false, false));
		cancel();
	}
}
