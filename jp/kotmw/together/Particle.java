package jp.kotmw.together;


import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.util.WoolColorEnum;
import net.minecraft.server.v1_10_R1.EnumParticle;
import net.minecraft.server.v1_10_R1.PacketPlayOutWorldParticles;

public class Particle extends BukkitRunnable {

	int tick = 0;
	Player player;
	double radius;
	double radius2;
	WoolColorEnum color;

	public Particle(Player player, int tick, double radius) {
		this.player = player;
		this.tick = tick;
		this.radius = radius;
		this.color = WoolColorEnum.LIME;
	}

	@Override
	public void run() {
		if(tick > 0)
		{
			Location l = player.getLocation();
			for(double i = 0 ; i < 360 ; i++) {
				double x = l.getX()+(radius*Math.sin(Math.toRadians(i)));
				double y = l.getY()+(radius*Math.sin(0));
				double z = l.getZ()+(radius*Math.cos(Math.toRadians(i)));
				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
				setValue(packet, "a", EnumParticle.REDSTONE);
				setValue(packet, "b", (float)x);
				setValue(packet, "c", (float)y+0.1f);
				setValue(packet, "d", (float)z);
				setValue(packet, "e", color.getRed());
				setValue(packet, "f", color.getGreen());
				setValue(packet, "g", color.getBlue());
				setValue(packet, "h", 1);
				setValue(packet, "i", 0);
				setValue(packet, "j", true);

				for(Player online : Bukkit.getOnlinePlayers())
					Main.sendPlayer(online, packet);
			}
			/*for(double i = 0 ; i < 360 ; i++) {
				double x = l.getX()+(radius2*Math.sin(Math.toRadians(i)));
				double y = l.getY()+(radius2*Math.sin(0));
				double z = l.getZ()+(radius2*Math.cos(Math.toRadians(i)));
				PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
						EnumParticle.REDSTONE
						, true
						, (float)x
						, (float)y + 2
						, (float)z
						, (color2.getRed() / 255) + ii
						, (color2.getGreen() / 255)
						, (color2.getBlue() / 255)
						, 10
						, 0
						, 0);
				for(Player online : Bukkit.getOnlinePlayers())
					Main.sendPlayer(online, packet);
			}*/
			tick--;
		}
		else
		{
			this.cancel();
		}
	}

	//NPCのパケットの基礎(完全コピペだけど気にしないで())

			/**
			 * @param obj Packet~~の変数
			 * @param name 指定したパケットのフィールド
			 * @param value 指定したフィールドに入れる新しい値
			 *
			 */
			public void setValue(Object obj, String name, Object value)
			{
				try {
					Field field = obj.getClass().getDeclaredField(name);
					field.setAccessible(true);
					field.set(obj, value);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			/**
			 * @param obj Packet~~の変数
			 * @param name 指定したパケットのフィールド
			 *
			 * @return フィールドの値を返す
			 *
			 */
			public Object getValue(Object obj, String name)
			{
				try {
					Field field = obj.getClass().getDeclaredField(name);
					field.setAccessible(true);
					return field.get(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

}
