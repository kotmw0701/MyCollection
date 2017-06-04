package jp.kotmw.together.bossmonster;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import jp.kotmw.together.Main;
import jp.kotmw.together.test2.Polar_coodinates;
import jp.kotmw.together.util.DetailsColor;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;

public class BossAttackRange_Rectangle extends BossAttackRange {

	private List<Location> locs = new ArrayList<>();
	private int move;
	private int maxradius;
	private int maxwidth;
	private double theta;
	private DetailsColor color = new DetailsColor("#ff7000");
	private DetailsColor color2 = new DetailsColor("#ff3000");
	
	public BossAttackRange_Rectangle(Boss boss, Location corner, int radius, int width, double theta) {
		super(boss);
		locs.add(corner);
		this.maxradius = radius*5;
		this.maxwidth = width*5;
		this.theta = theta;
	}

	@Override
	public void run() {
		while(!boss.getBoss().isDead() && !cancel) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(move >= maxradius*2)
				move = 0;
			for(int radius = 0; radius <= maxradius; radius+=2) {
				for(int width = -(maxwidth/2); width <= (maxwidth/2); width+=2) {
					if((radius != 0 && radius != maxradius) && (width != -(maxwidth/2) && width != (maxwidth/2)))
						continue;
					for(Location loc : locs) {
						loc = loc.clone() 
								.add(new Polar_coodinates(loc.getWorld(), radius*0.2, theta, 0).convertLocation())
								.add(new Polar_coodinates(loc.getWorld(), width*0.2, theta-Math.toRadians(90), 0).convertLocation());
						Main.sendPlayersParticle(EnumParticle.REDSTONE, 
								loc, 
								color2.getRed(), 
								color2.getGreen(), 
								color2.getBlue(),
								Bukkit.getOnlinePlayers());
					}
				}
			}
			for(int width = -(maxwidth/2)*2; width <= (maxwidth/2)*2; width+=2) {
				for(Location loc : locs) {
					loc = loc.clone()
							.add(new Polar_coodinates(loc.getWorld(), move*0.1, theta, 0).convertLocation())
							.add(new Polar_coodinates(loc.getWorld(), width*0.1, theta-Math.toRadians(90), 0).convertLocation());
					Main.sendPlayersParticle(EnumParticle.REDSTONE, 
							loc, 
							color.getRed(), 
							color.getGreen(), 
							color.getBlue(),
							Bukkit.getOnlinePlayers());
				}
			}
			move+=1;
		}
	}
	
	public void Expansion() throws InterruptedException {
		
	}

}
