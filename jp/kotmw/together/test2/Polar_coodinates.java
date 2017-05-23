package jp.kotmw.together.test2;

import org.bukkit.Location;
import org.bukkit.World;

public class Polar_coodinates {
	private World world;
	private double radius; //r
	private double theta; //θ ※ラジアン
	private double phi; //φ ※ラジアン
	
	public Polar_coodinates(World world, double radius, double theta, double phi) {
		this.world = world;
		this.radius = radius;
		this.theta = theta;
		this.phi = phi;
	}
	
	public Polar_coodinates(Location bukkitlocation) {
		this.radius = bukkitlocation.distance(new Location(bukkitlocation.getWorld(), 0, 0, 0));
		double x = bukkitlocation.getX(), y = bukkitlocation.getY(), z = bukkitlocation.getZ();
		this.theta = Math.acos(z/Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2)));
		this.phi = Math.atan(y/x);
	}
	
	public Location convertLocation() {
		double x = radius*Math.sin(theta)*Math.cos(phi);
		double y = radius*Math.sin(theta)*Math.sin(phi);
		double z = radius*Math.cos(theta);
		return new Location(world, x, y, z);
	}
	
	public World getWorld() {return world;}
	
	public double getRadius() {return radius;}
	
	public double getTheta() {return theta;}
	
	public double getPhi() {return phi;}

	public void setWorld(World world) {this.world = world;}

	public void setRadius(double radius) {this.radius = radius;}

	public void setTheta(double theta) {this.theta = theta;}

	public void setPhi(double phi) {this.phi = phi;}
}
