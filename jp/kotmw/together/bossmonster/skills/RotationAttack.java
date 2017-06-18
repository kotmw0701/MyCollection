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
	private int count = 2;
	private int speed = 26;
	private int minradius = 2;
	private DetailsColor color = DetailsColorType.WoolColor_BLUE.getColor();
	private DetailsColor color2 = DetailsColorType.WoolColor_ORANGE.getColor();
	
	public RotationAttack(Boss boss) {
		super(boss);
		setBossStatus(false, false);
		boss.sendChallengersMessage("ぐるぐる");
		reversal = new Random().nextBoolean();
		setParam();
		boss.sendChallengersTitle(0, 2, 0, ChatColor.RED.toString()+ChatColor.BOLD+(reversal ? "⟳" : "⟲"), "");
		for(double theta2 = 0.0 ; theta2 <= 2*Math.PI ; theta2 += Math.PI/45)
			sendReddust(boss.getBoss().getEyeLocation().clone().add(new Polar_coodinates(boss.getBoss().getLocation().getWorld(), 2, theta2, 0).convertLocation()).add(0, -0.5, 0), color2);
		for(double radius = minradius; radius <= 30; radius+=0.2) {
			for(double num = 0.0; num <= 2*Math.PI; num+=2*Math.PI/count) {
				Polar_coodinates pc = new Polar_coodinates(boss.getBoss().getLocation().getWorld(), radius, Math.toRadians(boss.getBoss().getLocation().getYaw())+theta+num, 0);
				sendReddust(boss.getBoss().getEyeLocation().clone().add(pc.convertLocation()).add(0, -0.5, 0),
						color2,
						true,
						14.0);
			}
		}
		runDelay(20);
	}

	@Override
	protected void fire() throws InterruptedException {
		while((reversal ? theta >= -2*Math.PI : theta <= 2*Math.PI)) {
			if(getBoss().isDead() || !isRunning()) {
				cancel();
				break;
			}
			for(double theta2 = 0.0 ; theta2 <= 2*Math.PI ; theta2 += Math.PI/30)
				sendReddust(boss.getBoss().getEyeLocation().clone().add(new Polar_coodinates(boss.getBoss().getLocation().getWorld(), 2, theta2, 0).convertLocation()).add(0, -0.5, 0), color2);
			for(double radius = minradius; radius <= 30; radius+=0.2) {
				for(double num = 0.0; num <= 2*Math.PI; num+=2*Math.PI/count) {
					Polar_coodinates pc = new Polar_coodinates(boss.getBoss().getLocation().getWorld(), radius, Math.toRadians(boss.getBoss().getLocation().getYaw())+theta+num, 0);
					sendReddust(boss.getBoss().getEyeLocation().clone().add(pc.convertLocation()).add(0, -0.5, 0),
							color,
							true,
							14.0);
				}
			}
			theta+=(reversal ? -Math.PI/180 : Math.PI/180);
			sleep(speed);
		}
		cancel();
	}
	
	private void setParam() {
		if((boss.getLevel() > 1) && (boss.getLevel() < 5)) {
			switch(boss.getLevel()) {
			case 2:
				speed = 24;
				break;
			case 3:
				speed = 22;
				break;
			case 4:
				speed = 20;
				break;
			}
		} else if((boss.getLevel() >= 5) && (boss.getLevel() < 8)) {
			count = 3;
			switch(boss.getLevel()) {
			case 5:
				speed = 22;
				break;
			case 6:
				speed = 20;
				break;
			case 7:
				speed = 18;
				break;
			}
		} else if(boss.getLevel() >= 8) {
			count = 4;
			switch(boss.getLevel()) {
			case 8:
				speed = 20;
				break;
			case 9:
				speed = 18;
				break;
			case 10:
				speed = 16;
				break;
			default:
				break;
			}
		}
		switch(boss.getPattern()) {
		case b:
			speed -= 2;
			break;
		case c:
			count +=1;
			speed -=4;
			break;
		case d:
			count +=2;
			speed -=6;
			break;
		case Exception:
			count = 20;
			speed = 1;
		default:
			break;
		}
	}
	
	/* パターン設定メモ
	 * 最大本数 : 6  (Exception 20)
	 * 最大速度 : 10 (Exception 1)
	 * 
	 *      本数: 速度(millisecond)
	 * Lv.1 : 2 : 26
	 * Lv.2 : 2 : 24
	 * Lv.3 : 2 : 22
	 * Lv.4 : 2 : 20
	 * Lv.5 : 3 : 22
	 * Lv.6 : 3 : 20
	 * Lv.7 : 3 : 18
	 * Lv.8 : 4 : 20
	 * Lv.9 : 4 : 18
	 * Lv.10: 4 : 16
	 * 
	 * 変化量
	 *    A : 0 : 0
	 *    B : 0 :-2
	 *    C :+1 :-4
	 *    D :+2 :-6
	 */
}
