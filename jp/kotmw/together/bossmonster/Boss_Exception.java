package jp.kotmw.together.bossmonster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.together.Main;

public class Boss_Exception extends Thread{
	
	private Boss boss;
	private String reload = "RELOAD..............................................", 
			safemode = "TRANSITING TO SAFE MODE............",
			recovery = "SYSTEM RECOVERY............................",
			restart = "RESTART............................................",
			cause = "GETTING CAUSE................................";
	@SuppressWarnings("unused")
	private int failrestart;
	private Running runmode = Running.RELOAD;
	private Loading loading = Loading.OK;
	private Random random = new Random();
	private int parcent;
	private boolean exception = true;
	
	
	public Boss_Exception(Boss boss) {
		this.boss = boss;
	}
	
	public boolean checkException() {
		Random random = new Random();
		if(random.nextInt(10) > 7)
			return true;
		return false;
	}
	
	@Override
	public void run() {
		List<String> msgs = new ArrayList<>();
		String loadingtxt = "";
		//エラー表示
		boss.sendChallengersTitle(1, 10, 0, ChatColor.RED+"-------ERROR-------", "");
		syncAddPotion();
		try {
			sleep(500);
			while(exception) {
				sleep(1000*3);
				switch(runmode) {
				case CAUSE:
					loadingtxt = cause;
					break;
				case RECOVERY:
					loadingtxt = recovery;
					break;
				case RELOAD:
					loadingtxt = reload;
					break;
				case RESTART:
					loadingtxt = restart;
					break;
				case TTSAFEMODE:
					loadingtxt = safemode;
					break;
				default:
					break;
				}
				while(parcent < 100) {
					sleep(200);
					if(parcent > 70 && (random.nextInt(10) == 0)) {
						sleep(1000*3);
						setText(msgs, loadingtxt+Loading.FAIL.toString());
						loading = Loading.FAIL;
						break;
					}
					parcent += random.nextInt((100-parcent)+1);
					setText(msgs, loadingtxt+parcent+"%");
				}
				if(parcent >= 100) {
					setText(msgs, loadingtxt+Loading.OK.toString());
					loading = Loading.OK;
				}
				msgs.add(loadingtxt+loading.toString());
				parcent = 0;
				runmode = nextRunning(runmode, loading);
				if(runmode == null)
					break;
			}
			/* 再読み込み、起動開始
			 * RELOAD
			 *   OK    -> GETTING CAUSE
			 *   FAILRE-> TRANSITING TO SAFE MODE
			 * 
			 * TRANSITING TO SAFE MODE
			 *   OK    -> RECOVERY
			 *   FAILRE-> GETTING CAUSE
			 * 
			 * RECOVERY
			 *   OK    -> RESTART
			 *   FAILRE-> GETTING CAUSE
			 *   
			 * GETTING CAUSE
			 *   OK    -> RECOVERY
			 *   FAILRE-> Exception
			 * 
			 * RESTART
			 *   OK    -> START
			 *   FAILRE-> TRANSITING TO SAFE MODE
			 * 
			 * 2回RESTARTが失敗したら強制的にException難易度突入
			 * 
			 */
			//成功 RESTART -> OK
			//失敗 Exception
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void syncAddPotion() {
		(new BukkitRunnable() {
			
			@Override
			public void run() {
				boss.getchallengers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3600*20, 1, false, false)));
			}
		}).runTask(Main.instance);
	}
	
	private void setText(List<String> completed, String loading) {
		if(completed.size() > 5)
			for(int i = 0; i<=4; i++)
				completed.remove(i);
		for(int i = 0; i <= 10; i++)
		boss.sendChallengersText("");//1～10
		//ブランク
		boss.sendChallengersText("");
		boss.sendChallengersText((completed.size() > 0 ? completed.get(0) : loading));
		boss.sendChallengersText("");
		boss.sendChallengersText((completed.size() > 1 ? completed.get(1) : (completed.size() == 1 ? loading : "")));
		boss.sendChallengersText("");
		boss.sendChallengersText((completed.size() > 2 ? completed.get(2) : (completed.size() == 2 ? loading : "")));
		boss.sendChallengersText("");
		boss.sendChallengersText((completed.size() > 3 ? completed.get(3) : (completed.size() == 3 ? loading : "")));
		boss.sendChallengersText("");
		boss.sendChallengersText((completed.size() > 4 ? completed.get(4) : (completed.size() == 4 ? loading : "")));
		boss.sendChallengersText("");
	}
	
	private Running nextRunning(Running now, Loading mode) {
		switch(now) {
		case CAUSE:
			switch(mode) {
			case FAIL:
				return null;
			case OK:
				return Running.RECOVERY;
			}
		case RECOVERY:
			switch(mode) {
			case FAIL:
				return Running.CAUSE;
			case OK:
				return Running.RESTART;
			}
		case RELOAD:
			switch(mode) {
			case FAIL:
				return Running.RECOVERY;
			case OK:
				return Running.CAUSE;
			}
		case RESTART:
			switch(mode) {
			case FAIL:
				return Running.TTSAFEMODE;
			case OK:
				return null;
			}
		case TTSAFEMODE:
			switch(mode) {
			case FAIL:
				return Running.CAUSE;
			case OK:
				return Running.RECOVERY;
			}
		default:
			break;
		}
		return null;
	}
	
	public enum Running {
		RELOAD, TTSAFEMODE, RECOVERY, CAUSE, RESTART;
	}
	
	public enum Loading {
		OK, FAIL
	}
}
