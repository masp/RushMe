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

import com.tips48.rushMe.arenas.Arena;
import com.tips48.rushMe.custom.items.GrenadeManager;
import com.tips48.rushMe.data.PlayerData;
import com.tips48.rushMe.gamemodes.GameMode;
import com.tips48.rushMe.gamemodes.GameModeType;
import com.tips48.rushMe.teams.Team;
import com.tips48.rushMe.util.RMLogging;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.getspout.spoutapi.SpoutManager;

import java.util.*;
import java.util.logging.Level;

public class GameManager {

    private static final Set<Arena> games = new HashSet<Arena>();
    private static final Set<GameMode> gameModes = new HashSet<GameMode>();
    private static GameMode defaultGameMode = null;

    private GameManager() {

    }

    public static void addToGame(Arena arena, Player player, Team prefered) {
	addToGame(arena, player.getEntityId(), prefered);
    }

    public static void addToGame(Arena arena, int player, Team prefered) {
	GrenadeManager.createGrenades(player);
	arena.addPlayer(player, prefered);
	RMLogging.debugLog(Level.INFO,
		"Added " + player + " to " + arena.getName());
    }

    public static void removeFromGame(Arena arena, Player player) {
	removeFromGame(arena, player.getEntityId());
    }

    public static void removeFromGame(Arena arena, int player) {
	if (arena.hasPlayer(player)) {
	    arena.removePlayer(player);
	    RMLogging.debugLog(Level.INFO, "Removed " + player + " from "
		    + arena.getName());
	}
    }

    public static boolean inGame(Player player) {
	return inGame(player.getEntityId());
    }

    public static boolean inGame(int player) {
	for (Arena a : games) {
	    for (int s : a.getPlayers().toArray()) {
		if (s == player) {
		    return true;
		}
	    }
	}
	return false;
    }

    public static Arena getArenaOf(Player player) {
	return getArenaOf(player.getEntityId());
    }

    public static Arena getArenaOf(int player) {
	for (Arena a : games) {
	    if (a.hasPlayer(player)) {
		return a;
	    }
	}
	return null;
    }

    public static Arena getArena(String name) {
	for (Arena a : games) {
	    if (a.getName().equalsIgnoreCase(name)) {
		return a;
	    }
	}
	return null;
    }

    public static Arena getArena(UUID uuid) {
	for (Arena a : games) {
	    if (a.getUUID().equals(uuid)) {
		return a;
	    }
	}
	return null;
    }

    public static void removeArena(Arena a) {
	a.stop();
	for (int player : a.getPlayers().toArray()) {
	    a.removePlayer(player);
	}
	a.onDelete();
	games.remove(a);
    }

    public static Arena createArena(String name, GameMode gamemode,
	    int creator, World world, UUID uuid) {
	Arena a = new Arena(gamemode, name, creator, world,
		uuid == null ? UUID.randomUUID() : uuid);
	games.add(a);

	RMLogging.debugLog(
		Level.INFO,
		"Created arena " + a.getName() + " with gamemode: "
			+ gamemode.getName() + ";Creator: " + a.getCreator()
			+ ";In world: " + world.getName());

	return a;
    }

    public static GameMode createGameMode(String name, GameModeType type,
	    Integer time, Boolean respawn, Integer respawnTime,
	    Integer maxPlayers, List<Team> teams, UUID uuid) {
	GameMode gm = new GameMode(name, type, time, respawn, respawnTime,
		maxPlayers, teams, uuid == null ? UUID.randomUUID() : uuid);

	gameModes.add(gm);

	RMLogging.debugLog(Level.INFO, "Created gamemode " + gm.getName()
		+ " with max players: " + gm.getMaxPlayers() + ";Time: " + time
		+ ";Type: " + type + ";Respawn: " + respawn + ";RespawnTime: "
		+ respawnTime);

	return gm;
    }

    public static GameMode getGameMode(String name) {
	for (GameMode g : gameModes) {
	    if (g.getName().equalsIgnoreCase(name)) {
		return g;
	    }
	}
	return null;
    }

    public static Set<String> getGameModeNames() {
	Set<String> list = new HashSet<String>();
	for (GameMode g : gameModes) {
	    list.add(g.getName());
	}
	return list;
    }

    public static GameMode getDefaultGameMode() {
	return defaultGameMode;
    }

    public static void setDefaultGameMode(GameMode g) {
	if (defaultGameMode == null) {
	    defaultGameMode = g;
	} else {
	    RMLogging
		    .log(Level.SEVERE,
			    "Something tried to change the default GameMode.  Make sure you don't have two default GameMode's or a rouge plugin.");
	}
    }

    public static Set<String> getArenaNames() {
	Set<String> names = new HashSet<String>();
	for (Arena a : games) {
	    names.add(a.getName());
	}
	return names;
    }

    public static GameMode getGameMode(UUID uuid) {
	for (GameMode gm : gameModes) {
	    if (gm.getUUID().equals(uuid)) {
		return gm;
	    }
	}
	return null;
    }

    public static void removeAll() {
	for (Arena a : games) {
	    removeArena(a);
	}
    }

    protected static PListener getPListener() {
	return new PListener();
    }

    private static class PListener extends PlayerListener {
	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
	    PlayerData.setDefaults(event.getPlayer());
	}
    }

    public static void updateNames(Arena arena) {
	RMLogging.debugLog(Level.INFO, "Updating names for " + arena.getName());
	new UpdateNames(arena).run();
    }

    private static class UpdateNames extends Thread {
	private Arena arena;

	public UpdateNames(Arena arena) {
	    this.arena = arena;
	}

	public void run() {
	    for (int i = 0; i <= arena.getTeams().size(); i++) {
		Team t = arena.getTeams().get(i);
		for (int player : t.getPlayers().toArray()) {
		    Player p = SpoutManager.getPlayerFromId(player);
		    if (p != null) {
			for (Player onlinePlayer : RushMe.getInstance()
				.getServer().getOnlinePlayers()) {
			    if (onlinePlayer == p) {
				continue;
			    }
			    Team team = arena.getTeamOf(onlinePlayer);
			    ChatColor color = ChatColor.WHITE;
			    if (team != null) {
				color = t.equals(team) ? ChatColor.GREEN : null;
			    }
			    if (color == null) {
				SpoutManager.getPlayer(p).setTitleFor(
					SpoutManager.getPlayer(onlinePlayer),
					"[hide]");
				continue;
			    }
			    SpoutManager.getPlayer(p).setTitleFor(
				    SpoutManager.getPlayer(onlinePlayer),
				    color + onlinePlayer.getName());
			}
		    }
		}
	    }
	}
    }
}