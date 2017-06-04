package jp.kotmw.together.bossmonster.skills;

import java.util.Random;

import org.bukkit.ChatColor;

import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.test2.Polar_coodinates;
import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.DetailsColor.DetailsColorType;

public class RotationAttack extends SkillBase {

	private double theta = 0.0;
	private boolean reversal = false;
	private DetailsColor color = DetailsColorType.WoolColor_BLUE.getColor();
	private DetailsColor color2 = DetailsColorType.WoolColor_ORANGE.getColor();
	
	public RotationAttack(Boss boss) {
		super(boss);
		boss.getBoss().setAI(false);
		boss.getBoss().setTarget(null);
		boss.sendChallengersMessage("ぐるぐる");
		boss.setPlayerTurn(false);
		reversal = new Random().nextBoolean();
		boss.sendChallengersTitle(0, 2, 0, ChatColor.RED.toString()+ChatColor.BOLD+(reversal ? "⟳" : "⟲"), "");
		for(double theta2 = 0.0 ; theta2 <= 2*Math.PI ; theta2 += Math.PI/45)
			sendReddust(boss.getBoss().getEyeLocation().clone().add(new Polar_coodinates(boss.getBoss().getLocation().getWorld(), 2, theta2, 0).convertLocation()).add(0, -0.5, 0), color2);
		runDelay(20);
	}

	@Override
	protected void fire() throws InterruptedException {		
		while((reversal ? theta >= -Math.PI : theta <= Math.PI)) {
			sleep(25);
			for(double theta2 = 0.0 ; theta2 <= 2*Math.PI ; theta2 += Math.PI/45)
				sendReddust(boss.getBoss().getEyeLocation().clone().add(new Polar_coodinates(boss.getBoss().getLocation().getWorld(), 2, theta2, 0).convertLocation()).add(0, -0.5, 0), color2);
			for(double radius = -20.0; radius <= 20; radius+=0.1) {
				if(radius >= -2.0 && radius <= 2.0)//ボスから1.5ブロック分の判定なし空間
					continue;
				Polar_coodinates pc = new Polar_coodinates(boss.getBoss().getLocation().getWorld(), radius, Math.toRadians(boss.getBoss().getLocation().getYaw())+theta, 0);
				sendReddust(boss.getBoss().getEyeLocation().clone().add(pc.convertLocation()).add(0, -0.5, 0),
						color,
						true,
						14.0);
			}
			theta+=(reversal ? -Math.PI/180 : Math.PI/180);
		}
		cancel();
	}
}
