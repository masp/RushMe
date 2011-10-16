package com.tips48.rushMe;

import java.util.logging.Logger;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;

import com.tips48.rushMe.custom.blocks.BlockManager;
import com.tips48.rushMe.custom.items.FireType;
import com.tips48.rushMe.custom.items.GunManager;
import com.tips48.rushMe.listeners.RMInputListener;
import com.tips48.rushMe.listeners.RMPlayerListener;

public class RushMe extends JavaPlugin {

	private final static String prefix = "[RushMe]";
	private final static double version = 0.1;
	private final static int subVersion = 0;
	private final static Logger log = Logger.getLogger("Minecraft");
	private final GunManager gManager = new GunManager();

	private static RushMe instance;

	public void onEnable() {
		instance = this;
		
		BlockManager.init();
		
		SpoutManager.getFileManager().addToPreLoginCache(this, "HAI");
		
		gManager.createGun("AK - 47", "HAI", 2 * 20, 30, 120,
				FireType.AUTOMATIC, 1, false, null, 15, 10);
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN,
				new RMPlayerListener(), Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN,
				GameManager.getPListener(), Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN,
				SpoutGUI.getPListener(), Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT,
				new RMPlayerListener(), Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT,
				new RMInputListener(), Priority.Normal, this);
		
		GameManager.createTeams();
		
		log(true, "RushMe Version " + version + "_" + subVersion + " enabled");
	}

	public void onDisable() {
		log(true, "Disabled");
	}

	public static RushMe getInstance() {
		return instance;
	}

	public static void log(boolean usePrefix, String message) {
		if (usePrefix) {
			log.info(prefix + " " + message);
		} else {
			log.info(message);
		}
	}

	public GunManager getGunManager() {
		return gManager;
	}
}
