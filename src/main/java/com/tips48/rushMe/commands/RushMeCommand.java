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

package com.tips48.rushMe.commands;

import com.tips48.rushMe.GameManager;
import com.tips48.rushMe.arenas.Arena;
import com.tips48.rushMe.gamemodes.GameMode;
import com.tips48.rushMe.teams.Team;
import com.tips48.rushMe.util.RMChat;
import com.tips48.rushMe.util.RMUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class RushMeCommand implements CommandExecutor {

    private static final TIntObjectMap<Arena> defining = new TIntObjectHashMap<Arena>();

    public boolean onCommand(CommandSender sender, Command cmd,
	    String commandLabel, String[] args) {
	if (args.length == 0) {
	    RMChat.sendMainCommand(sender);
	} else if (args.length == 1) {
	    if (args[0].equalsIgnoreCase("help")) {
		RMChat.sendHelp(sender);
	    } else if (args[0].equalsIgnoreCase("list")) {
		sender.sendMessage(ChatColor.RED + "Arena's:");
		sender.sendMessage(ChatColor.AQUA
			+ RMUtils.readableSet(GameManager.getArenaNames()));
	    } else {
		RMChat.sendWrongArguments(sender);
	    }
	} else if (args.length == 2) {
	    if (args[0].equalsIgnoreCase("join")) {
		if (!(sender instanceof Player)) {
		    RMChat.sendPlayerOnly(sender);
		    return true;
		}
		Player player = (Player) sender;
		if (GameManager.inGame(player)) {
		    player.sendMessage(ChatColor.RED
			    + "You are already in the arena: "
			    + GameManager.getArenaOf(player).getName());
		    player.sendMessage(ChatColor.RED
			    + "To leave use the command /RushMe leave "
			    + GameManager.getArenaOf(player).getName());
		    return true;
		}
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    GameManager.addToGame(a, player, null);
		    player.sendMessage(ChatColor.AQUA + "Joined the arena "
			    + a.getName());
		} else {
		    player.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		}
	    } else if (args[0].equalsIgnoreCase("leave")) {
		if (!(sender instanceof Player)) {
		    RMChat.sendPlayerOnly(sender);
		    return true;
		}
		Player player = (Player) sender;
		if (!GameManager.inGame(player)) {
		    player.sendMessage(ChatColor.RED
			    + "You aren't in an arena!");
		    return true;
		}
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    GameManager.removeFromGame(a, player);
		    player.sendMessage(ChatColor.AQUA + "Left the arena "
			    + a.getName());
		} else {
		    player.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		}
	    } else if (args[0].equalsIgnoreCase("create")) {
		if (!(sender instanceof Player)) {
		    RMChat.sendPlayerOnly(sender);
		    return true;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("RushMe.create")) {
		    RMChat.sendNoPermission(player);
		    return true;
		}
		if (GameManager.getArena(args[1]) != null) {
		    player.sendMessage(ChatColor.RED
			    + "There is already an arena with that name.  Please choose another");
		    return true;
		}
		Arena a = GameManager.createArena(args[1],
			GameManager.getDefaultGameMode(), player.getEntityId(),
			player.getWorld(), null);
		RMChat.sendArenaInfo(player, a);
		player.sendMessage(ChatColor.AQUA
			+ "Type /RushMe define <name> to start defining the Arena");
	    } else if (args[0].equalsIgnoreCase("delete")) {
		if (!(sender.hasPermission("RushMe.delete"))) {
		    RMChat.sendNoPermission(sender);
		    return true;
		}
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    GameManager.removeArena(a);
		    sender.sendMessage(ChatColor.AQUA + args[1] + " deleted.");
		} else {
		    sender.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		}
	    } else if (args[0].equalsIgnoreCase("info")) {
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    RMChat.sendArenaInfo(sender, a);
		} else {
		    sender.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		}
	    } else if (args[0].equalsIgnoreCase("start")) {
		if (!(sender.hasPermission("RushMe.start"))) {
		    RMChat.sendNoPermission(sender);
		    return true;
		}
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    a.start();
		    sender.sendMessage(ChatColor.AQUA + a.getName()
			    + " started");
		} else {
		    sender.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		}
	    } else if (args[0].equalsIgnoreCase("stop")) {
		if (!(sender.hasPermission("RushMe.stop"))) {
		    RMChat.sendNoPermission(sender);
		    return true;
		}
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    a.stop();
		    sender.sendMessage(ChatColor.AQUA + a.getName()
			    + " stopped");
		} else {
		    sender.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		}
	    } else if (args[0].equalsIgnoreCase("define")) {
		if (!(sender instanceof Player)) {
		    RMChat.sendPlayerOnly(sender);
		    return true;
		}
		Player player = (Player) sender;
		if (!(player.hasPermission("RushMe.define"))) {
		    RMChat.sendNoPermission(player);
		    return true;
		}
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    defining.put(player.getEntityId(), a);
		    player.sendMessage(ChatColor.AQUA
			    + "Right click twice to select the two points.");
		    return true;
		} else {
		    player.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		    return true;
		}
	    } else if (args[0].equalsIgnoreCase("done")) {
		if (!(sender instanceof Player)) {
		    RMChat.sendPlayerOnly(sender);
		    return true;
		}
		Player player = (Player) sender;
		if (!(player.hasPermission("RushMe.done"))) {
		    RMChat.sendNoPermission(player);
		    return true;
		}
		Arena a = GameManager.getArena(args[1]);
		if (a != null) {
		    defining.remove(player.getEntityId());
		    player.sendMessage(ChatColor.AQUA + "Done defining "
			    + a.getName());
		    return true;
		} else {
		    player.sendMessage(ChatColor.RED
			    + "No arena with the name " + args[1]);
		    return true;
		}
	    } else {
		RMChat.sendWrongArguments(sender);
	    }
	    return true;
	} else if (args.length == 3) {
	    if (args[0].equalsIgnoreCase("create")) {
		if (!(sender instanceof Player)) {
		    RMChat.sendPlayerOnly(sender);
		    return true;
		}
		Player player = (Player) sender;
		if (!sender.hasPermission("RushMe.create")) {
		    RMChat.sendNoPermission(player);
		    return true;
		}
		if (GameManager.getArena(args[1]) != null) {
		    player.sendMessage(ChatColor.RED
			    + "There is already an arena with that name.  Please choose another");
		    return true;
		}
		GameMode g = GameManager.getGameMode(args[2]);
		if (g == null) {
		    player.sendMessage(ChatColor.RED + args[2]
			    + " is not a valid GameMode.");
		    player.sendMessage(ChatColor.RED
			    + "Valid GameModes: "
			    + RMUtils.readableSet(GameManager
				    .getGameModeNames()));
		    return true;
		}
		Arena a = GameManager.createArena(args[1], g,
			player.getEntityId(), player.getWorld(), null);
		RMChat.sendArenaInfo(player, a);
		player.sendMessage(ChatColor.AQUA
			+ "Type /RushMe define <name> to start defining the Arena");
	    } else if (args[0].equalsIgnoreCase("join")) {
		if (args[0].equalsIgnoreCase("join")) {
		    if (!(sender instanceof Player)) {
			RMChat.sendPlayerOnly(sender);
			return true;
		    }
		    Player player = (Player) sender;
		    if (GameManager.inGame(player)) {
			player.sendMessage(ChatColor.RED
				+ "You are already in the arena: "
				+ GameManager.getArenaOf(player).getName());
			player.sendMessage(ChatColor.RED
				+ "To leave use the command /RushMe leave "
				+ GameManager.getArenaOf(player).getName());
			return true;
		    }
		    Arena a = GameManager.getArena(args[1]);
		    if (a != null) {
			Team t = a.getTeam(args[2]);
			if (t == null) {
			    player.sendMessage(ChatColor.RED
				    + "There is no team with the name "
				    + args[2]);
			    player.sendMessage(ChatColor.RED + "Teams:");
			    player.sendMessage(ChatColor.RED
				    + RMUtils.readableList(a.getTeams()));
			    return true;
			}
			GameManager.addToGame(a, player, t);
			player.sendMessage(ChatColor.AQUA + "Joined the arena "
				+ a.getName());
		    } else {
			player.sendMessage(ChatColor.RED
				+ "No arena with the name " + args[1]);
		    }
		}
	    } else {
		RMChat.sendWrongArguments(sender);
	    }
	} else {
	    RMChat.sendTooManyArguments(sender);
	}
	return true;
    }

    public static boolean isDefining(Player player) {
	return isDefining(player.getEntityId());
    }

    public static boolean isDefining(int player) {
	return defining.containsKey(player);
    }

    public static Arena getDefining(Player player) {
	return getDefining(player.getEntityId());
    }

    public static Arena getDefining(int player) {
	return defining.get(player);
    }

    public static void remove(Player player) {
	remove(player.getEntityId());
    }

    public static void remove(int player) {
	defining.remove(player);
    }

}
