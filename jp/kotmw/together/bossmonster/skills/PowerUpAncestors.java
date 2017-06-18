package jp.kotmw.together.bossmonster.skills;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.test2.Polar_coodinates;
import jp.kotmw.together.util.DetailsColor.DetailsColorType;

public class PowerUpAncestors extends SkillBase {

	private List<Creature> ancestors = new ArrayList<>();
	
	public PowerUpAncestors(Boss boss, int num) {
		super(boss);
		resetLocation();
		setBossStatus(false, false);
		syncThread(() -> {
			Polar_coodinates pc = new Polar_coodinates(boss.getBoss().getWorld(), 10, 0, 0);
			for(double basetheta = 0.0; basetheta < 2*Math.PI; basetheta+= 2*Math.PI/num) {
				pc.setTheta(basetheta);
				Zombie zombie = (Zombie) boss.getBoss().getWorld().spawnEntity(boss.getCenterLocation().add(pc.convertLocation()), EntityType.ZOMBIE);
				zombie.setBaby(true);
				zombie.setAI(false);
				zombie.setMaxHealth(100);
				zombie.setHealth(zombie.getMaxHealth());
				AttributeInstance gkr = zombie.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
				gkr.addModifier(new AttributeModifier("generic.knockbackResistance", 1.0, Operation.ADD_NUMBER));
				ancestors.add(zombie);
			}
		});
		start();
	}

	@Override
	protected void fire() throws InterruptedException {
		while(!alldead() && !boss.getBoss().isDead()) {
			ancestors.forEach(entity -> {
				Location loc1 = boss.getBoss().getLocation(), loc2 = entity.getLocation();
				for(double radius = 0.3; radius <= loc1.distance(loc2); radius+=0.5) {
					Polar_coodinates pc = new Polar_coodinates(loc1.getWorld(), radius, Math.atan2(loc2.getX()-loc1.getX(), loc2.getZ()-loc1.getZ()), 0);
					sendReddust(loc1.clone().add(pc.convertLocation()).add(0, 1, 0), DetailsColorType.Navy_Blue.getColor());
				}
			});
			if(boss.getBoss().getMaxHealth() < boss.getBoss().getHealth() + (0.1*ancestors.size()))
				boss.getBoss().setHealth(boss.getBoss().getMaxHealth());
			else
				boss.getBoss().setHealth(boss.getBoss().getHealth()+(0.1*ancestors.size()));
			sleep(100);
		}
		if(boss.getBoss().isDead() && ancestors.size() > 0)
			ancestors.forEach(entity -> entity.remove());
		cancel();
	}
	
	private boolean alldead() {
		if(ancestors.size() == 0)
			return true;
		ancestors = ancestors.stream().filter(entity -> !entity.isDead()).collect(Collectors.toList());
		return false;
	}
}
