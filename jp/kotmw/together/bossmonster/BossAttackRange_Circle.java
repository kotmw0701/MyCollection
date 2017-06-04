package jp.kotmw.together.bossmonster;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import jp.kotmw.together.test2.Polar_coodinates;
import jp.kotmw.together.util.DetailsColor;

public class BossAttackRange_Circle extends BossAttackRange {

	private List<Location> center = new ArrayList<>();
	private double radius;
	private double radius_;
	private int expantiontick;
	private int showtick;
	private DetailsColor color = new DetailsColor("#ff5000");
	private DetailsColor color2 = new DetailsColor("#ff3000");
	
	
	public BossAttackRange_Circle(Boss boss, Location center, double radius) {
		super(boss);
		this.center.add(center);
		this.radius = radius;
		this.expantiontick = 10;
		this.showtick = 50;
	}
	
	public BossAttackRange_Circle(Boss boss, Location center, double radius, DetailsColor color1, DetailsColor color2) {
		super(boss);
		this.center.add(center);
		this.radius = radius;
		this.expantiontick = 10;
		this.showtick = 50;
		this.color = color1;
		this.color2 = color2;
	}
	
	public BossAttackRange_Circle(Boss boss, Location center, double radius, int param1, int param2) {
		super(boss);
		this.center.add(center);
		this.radius = radius;
		this.expantiontick = param1;
		this.showtick = param2;
	}
	
	public BossAttackRange_Circle(Boss boss, Location center, double radius, int param1, int param2, DetailsColor color1, DetailsColor color2) {
		super(boss);
		this.center.add(center);
		this.radius = radius;
		this.expantiontick = param1;
		this.showtick = param2;
		this.color = color1;
		this.color2 = color2;
	}
	
	public BossAttackRange_Circle(Boss boss, List<Location> center, double radius, int param1, int param2) {
		super(boss);
		this.center = center;
		this.radius = radius;
		this.expantiontick = param1;
		this.showtick = param2;
	}
	
	public BossAttackRange_Circle(Boss boss, List<Location> center, double radius, int param1, int param2, DetailsColor color1, DetailsColor color2) {
		super(boss);
		this.center = center;
		this.radius = radius;
		this.expantiontick = param1;
		this.showtick = param2;
		this.color = color1;
		this.color2 = color2;
	}
	
	@Override
	public void run() {
		try {
			Expansion();
			while(!boss.getBoss().isDead() && !cancel) {
				Thread.sleep(showtick);
				if(radius_ >= radius)
					radius_ = 0;
				for(Location center : this.center) {
					for(double theta = 0.0; theta <= 2*Math.PI; theta+=Math.PI/90) {
						setAttackRange(center.clone().add(new Polar_coodinates(center.getWorld(), radius_, theta, 0).convertLocation()), color);
						setAttackRange(center.clone().add(new Polar_coodinates(center.getWorld(), radius, theta, 0).convertLocation()), color2);
					}
				}
				radius_+=0.2;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void Expansion() throws InterruptedException {
		for(double radius = 0.0; radius <= this.radius; radius+=0.2) {
			for(double theta = 0.0; theta <= 2*Math.PI; theta+=Math.PI/90)
				for(Location center : this.center)
					setAttackRange(center.clone().add(new Polar_coodinates(center.getWorld(), radius, theta, 0).convertLocation()), color2);
			Thread.sleep(expantiontick);
		}
	}
}
