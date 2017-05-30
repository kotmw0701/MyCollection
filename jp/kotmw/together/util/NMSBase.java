/**
 * @author kotmw0701
 *
 */
package jp.kotmw.together.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSBase {

	protected static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object pConnection = handle.getClass().getField("playerConnection").get(handle);
			pConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(pConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static Class<?> getNMSClass(String name) {
		String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server."+ver+"."+name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
