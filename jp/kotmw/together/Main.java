package jp.kotmw.together;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import jp.kotmw.together.bossmonster.Boss;
import jp.kotmw.together.bossmonster.BossListeners;
import jp.kotmw.together.bossmonster.Boss_Exception;
import jp.kotmw.together.bossmonster.skills.PowerUpAncestors;
import jp.kotmw.together.getvisibleplayer.Test1;
import jp.kotmw.together.test2.Test2;
import jp.kotmw.together.test3.Turret;
import jp.kotmw.together.util.ParticleAPI;
import jp.kotmw.together.util.ParticleAPI.EnumParticle;
import jp.kotmw.together.util.Title;

public class Main extends JavaPlugin implements Listener {

	public static Main instance;
	public static boolean bossdebug;
	String Collectmovemeta = "CollectPluginMeta";
	Map<String, Location> checkloc = new HashMap<>();
	static Map<Location, Integer> rt = new HashMap<>();
	static Map<String, List<Location>> rh = new HashMap<>();
	static List<Location> rb = new ArrayList<>();
	public String filepath = getDataFolder() + File.separator;
	public File dir = new File(filepath + "mazes");
	public File config = new File(filepath + "Config.yml");
	public Boss boss;

	@Override
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new Test1(), this);
		getServer().getPluginManager().registerEvents(new Test2(), this);
		getServer().getPluginManager().registerEvents(new BossListeners(), this);
		if(!config.exists()) {
			this.getConfig().addDefault("Test", "test");
			this.getConfig().options().copyDefaults(true);
			this.saveConfig();
			this.reloadConfig();
		}
		if(!dir.exists())
			dir.mkdir();
		LoadMazeData.loadLoc();
	}

	@Override
	public void onDisable() {

	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender s, Command cmd, String lav, String[] args) {
		if(args.length >= 1) {
			if(s instanceof Player) {
				Player p = (Player)s;
				if((args.length == 1) && ("place".equalsIgnoreCase(args[0]))) {
					p.setMetadata(Collectmovemeta, new FixedMetadataValue(this, p.getName()));
				} else if((args.length == 1) && ("turret".equalsIgnoreCase(args[0]))) {
					Turret turret = new Turret((ArmorStand) p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND));
					turret.runTaskTimer(Main.instance, 3*20, 20);
				} else if((args.length == 1) && ("boss_spawn".equalsIgnoreCase(args[0]))) {
					if(Main.instance.boss != null && Main.instance.boss.isStarted())
						return false;
					if(Main.instance.boss != null && !Main.instance.boss.getBoss().isDead())
						Main.instance.boss.getBoss().remove();
					Main.instance.boss = new Boss(p.getLocation(), "TestBoss");
				} else if((args.length == 1) && ("boss_start".equalsIgnoreCase(args[0]))) {
					if(Main.instance.boss == null) {
						p.sendMessage("ボスが設置されていません");
						return false;
					}
					if(Main.instance.boss.isStarted())
						return false;
					Main.instance.boss.start();
				} else if((args.length == 1) && ("boss_kill".equalsIgnoreCase(args[0]))) {
					if(Main.instance.boss == null) {
						p.sendMessage("ボスが設置されていません");
						return false;
					}
					Main.instance.boss.kill();
					Main.instance.boss = null;
				} else if((args.length == 1) && ("font").equalsIgnoreCase(args[0])){ 
					Title.sendTitle(p, 0, 600, 0, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "");
				} else if((args.length == 1) && ("font2").equalsIgnoreCase(args[0])){
					Title.sendTitle(p, 0, 600, 0, "abcdefghijklmnopqrstuvwxyz", "");
				} else if((args.length == 1) && ("boss_ai").equalsIgnoreCase(args[0])) { 
					Vector vec1 = boss.getBoss().getLocation().getDirection(); 
					boss.getBoss().setVelocity(new Vector(-vec1.getX(), -vec1.getY(), -vec1.getZ()));
					boss.getBoss().setTarget(null);
					boss.getBoss().setAI(false);
				} else if((args.length == 1) && ("boss_debug").equalsIgnoreCase(args[0])) {
					p.sendMessage("[BossSystem] デバッグモードを"+!bossdebug+"に変更しました");
					bossdebug = !bossdebug;
					return true;
				} else if((args.length == 1) && ("boss_exception").equalsIgnoreCase(args[0])){
					if(Main.instance.boss == null) {
						p.sendMessage("ボスが設置されていません");
						return false;
					}
					new Boss_Exception(Main.instance.boss).start();
				} else if((args.length == 2) && ("boss_an".equalsIgnoreCase(args[0]))) {
					if(Main.instance.boss == null) {
						p.sendMessage("ボスが設置されていません");
						return false;
					}
					new PowerUpAncestors(Main.instance.boss, Integer.valueOf(args[1]));
				} else if((args.length == 1) && ("stop".equalsIgnoreCase(args[0]))) {
					if(p.hasMetadata(Collectmovemeta))
						p.removeMetadata(Collectmovemeta, this);
				} else if((args.length == 2) && ("getitem".equalsIgnoreCase(args[0]))){
					p.sendMessage(Material.getMaterial(args[1]).toString());
				} else if((args.length == 2) && ("particle".equalsIgnoreCase(args[0]))) {
					new Particle(p, 100, Double.valueOf(args[1])).runTaskTimer(this, 0, 2);
				} else if((args.length == 1) && ("setstand".equalsIgnoreCase(args[0]))){
					setStand(p.getLocation());
				} else if((args.length == 1) && ("breplace".equalsIgnoreCase(args[0]))) {
					WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
					Selection selection = worldEdit.getSelection(p);
					if(selection != null) {
						World w = selection.getWorld();
						int x1b = selection.getMinimumPoint().getBlockX();
						int y1b = selection.getMinimumPoint().getBlockY();
						int z1b = selection.getMinimumPoint().getBlockZ();
						int x2b = selection.getMaximumPoint().getBlockX();
						int y2b = selection.getMaximumPoint().getBlockY();
						int z2b = selection.getMaximumPoint().getBlockZ();
						int x1 = x1b, x2 = x2b;
						if(x1b < x2b) {
							x1 = x2b;
							x2 = x1b;
						}
						int y1 = y1b, y2 = y2b;
						if(y1b < y2b) {
							y1 = y2b;
							y2 = y1b;
						}
						int z1 = z1b, z2 = z2b;
						if(z1b < z2b) {
							z1 = z2b;
							z2 = z1b;
						}
						w.getBlockAt(x1, y1, z1).setType(Material.IRON_BLOCK);
						w.getBlockAt(x2, y2, z2).setType(Material.DIAMOND_BLOCK);
					}
				} else if((args.length == 2) && ("circle".equalsIgnoreCase(args[0]))) {
					int radius = Integer.valueOf(args[1]);
					Location l = p.getLocation();
					for(int i = 0 ; i < 360 ; i++) {
						int x = (int) (l.getBlockX()+(radius*Math.sin(i)));
						int z = (int) (l.getBlockZ()+(radius*Math.cos(i)));
						l.getWorld().getBlockAt(x, l.getBlockY()+2, z).setType(Material.SMOOTH_BRICK);
					}
				} else if((args.length == 3) && ("invisiblemaze".equalsIgnoreCase(args[0]))) {
					String sn = args[2];
					if(("setup".equalsIgnoreCase(args[1]))) {
						WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
						Selection selection = worldEdit.getSelection(p);
						if(selection != null) {
							World w = selection.getWorld();
							String worldName = w.getName();
							int x1 = selection.getMinimumPoint().getBlockX();
							int y1 = selection.getMinimumPoint().getBlockY();
							int z1 = selection.getMinimumPoint().getBlockZ();
							int x2 = selection.getMaximumPoint().getBlockX();
							int y2 = selection.getMaximumPoint().getBlockY();
							int z2 = selection.getMaximumPoint().getBlockZ();
							
							File file = new File(getDataFolder() + File.separator + "mazes" + File.separator + sn + ".yml");
							String sp = "\r\n";
							try {
								file.createNewFile();
							} catch (IOException e) {
								System.out.println("ファイルが生成できません！");
								System.out.println(e);
							}
							try {
								FileWriter writer = new FileWriter(file);
								for(int x = x1 ; x <= x2 ; x++) {
									for(int y = y1 ; y <= y2 ; y++) {
										for(int z = z1 ; z <= z2 ; z++) {
											Material type = w.getBlockAt(x, y, z).getType();
											if(!type.equals(Material.AIR))
												writer.write(worldName+"/"+x+"/"+y+"/"+z+"/"+type.toString()+ sp);
										}
									}
								}
								writer.close();
							} catch (IOException e) {
								System.out.println("セーブファイルの作成ができませんでした");
								System.out.println(e);
							}
							LoadMazeData.loadLoc();
							for(Location l : LoadMazeData.getLocationList(sn)) {
								FallingBlock fb = l.getWorld().spawnFallingBlock(l, LoadMazeData.getBlockData(sn, l), (byte)0);
								fb.setDropItem(false);
								l.getBlock().setType(Material.AIR);
								new RemoveFallingBlock(fb).runTaskLater(this, 10);
							}
							return true;
						}
					} else if("Realization".equalsIgnoreCase(args[1])) {
						List<Location> ll = LoadMazeData.getLocationList(sn);
						if(ll == null) {
							s.sendMessage("そのステージは存在しません");
							return false;
						}
						for(Location l : ll) {
							l.getBlock().setType(LoadMazeData.getBlockData(sn, l));
						}
						s.sendMessage("ブロックを可視化しました");
					} else if("Abstraction".equalsIgnoreCase(args[1])) {
						List<Location> ll = LoadMazeData.getLocationList(sn);
						if(ll == null) {
							s.sendMessage("そのステージは存在しません");
							return false;
						}
						for(Location l : ll) {
							FallingBlock fb = l.getWorld().spawnFallingBlock(l, LoadMazeData.getBlockData(sn, l), (byte)0);
							fb.setDropItem(false);
							l.getBlock().setType(Material.AIR);
							new RemoveFallingBlock(fb).runTaskLater(this, 10);
						}
						s.sendMessage("ブロックを不可視化しました");
					} else if("delete".equalsIgnoreCase(args[1])) {
						List<Location> ll = LoadMazeData.clearLocData(sn);
						Map<Location, Material> lb = LoadMazeData.clearBlockData(sn);
						if(ll == null) {
							s.sendMessage("そのステージは存在しません");
							return false;
						}
						for(Location l : ll) {
							l.getBlock().setType(lb.get(l));
						}
						File file = new File(getDataFolder() + File.separator + "mazes" + File.separator + sn +".yml");
						if(!file.exists()) {
							s.sendMessage("ファイルが存在しません");
							return false;
						}
						file.delete();
					}
				}
			}
		}
		return false;
	}


	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata(Collectmovemeta)) {
			Location l = p.getLocation().clone();
			int x = l.getBlockX() ,y = l.getBlockY() ,z = l.getBlockZ();
			Material m = Material.SMOOTH_BRICK;
			int x_ = x;
			int z_ = z;
			if(checkloc.containsKey(p.getName())) {
				x_ = checkloc.get(p.getName()).getBlockX();
				z_ = checkloc.get(p.getName()).getBlockZ();
			}
			if(x_ == x && z_ == z) {
				checkloc.put(p.getName(), LocConversion(l));
				return;
			}
			for(int xf = x-1 ; xf <= x+1 ; xf++) {
				for(int yf = y-1 ; yf <= y+1 ; yf++) {
					for(int zf = z-1 ; zf <= z+1 ; zf++) {
						if(yf == y-1 || (xf == x-1 || xf == x+1)) {
							l.getWorld().getBlockAt(xf, yf, zf).setType(m);
							Location sl = new Location(l.getWorld(), xf, yf, zf);
							rt.put(sl, 20*10+5);
							if(!rb.contains(sl)) {
								rb.add(sl);
								new RemoveBlock(sl, m).runTaskTimer(this, 0, 1);
							}
						}
					}
				}
			}
			checkloc.put(p.getName(), LocConversion(l));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMoveinArea(PlayerMoveEvent e) {
		String n = e.getPlayer().getName();
		Location l = e.getPlayer().getLocation().clone();
		List<Location> ll = new ArrayList<>();
		for(int x = l.getBlockX()-1 ; x <= l.getBlockX()+1 ; x++) {
			for(int y = l.getBlockY()-1 ; y <= l.getBlockY()+1 ; y++) {
				for(int z = l.getBlockZ()-1 ; z <= l.getBlockZ()+1 ; z++) {
					Location sl = new Location(l.getWorld(), x, y, z);
					for(String stage : LoadMazeData.getStageList()) {
						if(LoadMazeData.getLocationList(stage).contains(sl)) {
							sl.getBlock().setType(LoadMazeData.getBlockData(stage, sl));
							ll.add(sl);

							/*rt.put(sl, 20*2+10);
							if(!rb.contains(sl)) {
								rb.add(sl);
								new RemoveBlock(sl, Material.SMOOTH_BRICK).runTaskTimer(this, 0, 1);
							}*/
						}
					}
				}
			}
		}
		if(rh.containsKey(n)) {
			for(Location bl : rh.get(n)) {
				if(ll.contains(bl))
					continue;
				FallingBlock fb = bl.getWorld().spawnFallingBlock(bl, bl.getBlock().getType(), (byte)0);
				bl.getBlock().setType(Material.AIR);
				fb.setDropItem(false);
				new RemoveFallingBlock(fb).runTaskLater(this, 10);
			}
		}
		rh.put(n, ll);
	}

	@EventHandler
	public void onBlake(BlockBreakEvent e) {
		Location l = e.getBlock().getLocation();
		if(rb.contains(l)) {
			e.setCancelled(true);
		}
		for(String stage : LoadMazeData.getStageList()) {
			if(LoadMazeData.getLocationList(stage).contains(l)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() == null)
			return;
		Location l = e.getClickedBlock().getLocation().clone();
		for(String stage : LoadMazeData.getStageList()) {
			if(LoadMazeData.getLocationList(stage).contains(l)) {
				e.setCancelled(true);
			}
		}
	}

	public Location LocConversion(Location l) {
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}


	public void setStand(Location l) {
		float yaw = -l.getYaw(),pitch = -l.getPitch();
		double xo = l.getX(), yo = l.getY(), zo = l.getZ();
		int max = 50;
		double pInterval = 0.5;
		final List<ArmorStand> stands = new ArrayList<>();
		for(double i = pInterval; i <= max; i = i + pInterval) {
			double x = xo+(i*Math.sin(Math.toRadians(yaw)));
			double y = yo+(i*Math.sin(Math.toRadians(pitch)));
			double z = zo+(i*Math.cos(Math.toRadians(yaw)));
			ArmorStand armor = (ArmorStand) l.getWorld().spawnEntity(new Location(l.getWorld(), x, y, z), EntityType.ARMOR_STAND);
			armor.setVisible(false);
			armor.setGravity(false);
			ItemStack helm = new ItemStack(Material.STAINED_GLASS, 1, (short)1);
			armor.setHelmet(helm);
			stands.add(armor);
		}
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				for(ArmorStand stand : stands)
					stand.remove();
				stands.clear();
			}
		}, 20*5);
	}
	
	public static void sendPlayersParticle(EnumParticle type, Location center, float param1, float param2, float param3, Collection<? extends Player> collection) {
		collection.stream().filter(player -> center.getWorld().getName().equals(player.getLocation().getWorld().getName())).forEach(player -> {
			new ParticleAPI.Particle(type, 
					center, 
					param1, 
					param2, 
					param3, 
					1, 
					0).sendParticle(player);
		});
	}


	/**
	 * パケットを送信
	 *
	 * @param player 対象
	 * @param packet パケット
	 */
	@SuppressWarnings("rawtypes")
	public static void sendPlayer(Player player, net.minecraft.server.v1_10_R1.Packet packet)
	{
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}

	/**
	 * ファイルの保存
	 *
	 * @param fileconfiguration ファイルコンフィグを指定
	 * @param file ファイル指定
	 * @param save 上書きをするかリセットするか
	 */
	public void SettingFiles(FileConfiguration fileconfiguration, File file, boolean save)
	{
		if(!file.exists() || save)
		{
			try {
				fileconfiguration.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
