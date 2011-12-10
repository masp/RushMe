/*
 * This file is part of RushMe.
 *
 * RushMe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RushMe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.tips48.rushMe;

import com.randomappdev.pluginstats.Ping;
import com.tips48.rushMe.commands.RushMeCommand;
import com.tips48.rushMe.configuration.GameModeConfiguration;
import com.tips48.rushMe.configuration.GunConfiguration;
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.custom.items.*;
import com.tips48.rushMe.listeners.*;
import com.tips48.rushMe.packets.*;
import com.tips48.rushMe.util.RMLogging;
import com.tips48.rushMe.util.RMUtils;

import me.kalmanolah.cubelist.classfile.cubelist;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.io.AddonPacket;

import java.io.File;
import java.util.logging.Level;

public class RushMe extends JavaPlugin {

	private final static String prefix = "[RushMe]";
	private final static double version = 0.1;
	private final static int subVersion = 0;

	private RMInputListener inputListener;
	private RMPlayerListener playerListener;
	private RMEntityListener entityListener;

	private static RushMe instance;

	@Override
	public void onLoad() {
		instance = this;
		inputListener = new RMInputListener();
		playerListener = new RMPlayerListener();
		entityListener = new RMEntityListener();
	}

	public void onEnable() {

		RMLogging.setFile(this.getDataFolder().getPath() + File.separator
				+ "RushMeLog.log");
		RMLogging.setDebugFile(this.getDataFolder().getPath() + File.separator
				+ "RushMeDebug.log");
		RMLogging.setDebug(true);

		GunConfiguration.loadGuns();
		GameModeConfiguration.loadGameModes();

		registerEvents();
		registerPackets();

		getCommand("RushMe").setExecutor(new RushMeCommand());

		Ping.init(this);
		new cubelist(this);

		GrenadeManager.createGrenade("TestGrenade1234", "Test", "Bleh",
				GrenadeType.CONCUSSION, 3, 1, 2, 5, 5, null);

		RMLogging.log(Level.INFO, "RushMe Version " + version + "_"
				+ subVersion + " enabled");
		RMLogging
				.log(Level.INFO,
						"Guns loaded: "
								+ RMUtils.readableSet(GunManager.getGunNames()));
		RMLogging
				.log(Level.INFO,
						"Grenades loaded: "
								+ RMUtils.readableSet(GrenadeManager
										.getGrenadeNames()));
		RMLogging.log(
				Level.INFO,
				"GameModes loaded: "
						+ RMUtils.readableSet(GameManager.getGameModeNames()));
	}

	public void onDisable() {
		GameManager.removeAll();
		RMLogging.log(Level.INFO, "Disabled");
		RMLogging.shutdown();
	}

	public static RushMe getInstance() {
		return instance;
	}

	public static double getVersion() {
		return version;
	}

	public static int getSubVersion() {
		return subVersion;
	}

	public static String getPrefix() {
		return prefix;
	}

	private void registerEvents() {
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN,
				playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_MOVE,
				playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT,
				playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_KICK,
				playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_LOGIN,
				GameManager.getPListener(), Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN,
				SpoutGUI.getPListener(), Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_INTERACT,
				playerListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.CUSTOM_EVENT,
				inputListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_ITEM_HELD,
				playerListener, Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Type.ENTITY_DAMAGE,
				entityListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Type.ENTITY_REGAIN_HEALTH,
				entityListener, Priority.Normal, this);
	}

	private void registerPackets() {
		AddonPacket.register(PacketGrenadeUpdate.class, "GrenadeUpdate");
		AddonPacket.register(PacketGunUpdate.class, "GunUpdate");
		AddonPacket.register(PacketPlayerDataUpdate.class, "PlayerDataUpdate");
		AddonPacket.register(PacketToggleHUD.class, "ToggleHUD");
		AddonPacket.register(PacketUpdateHUD.class, "UpdateHUD");
		AddonPacket.register(PacketQueueKill.class, "QueueKill");
		AddonPacket.register(PacketQueuePoints.class, "QueuePoints");
		AddonPacket.register(PacketShowHit.class, "ShowHit");
		AddonPacket.register(PacketDoConcussion.class, "DoConcussion");
		AddonPacket.register(PacketArenaUpdate.class, "ArenaUpdate");
		AddonPacket.register(PacketGameModeCreate.class, "GameModeCreate");
		AddonPacket.register(PacketTeamUpdate.class, "TeamUpdate");
	}

}
