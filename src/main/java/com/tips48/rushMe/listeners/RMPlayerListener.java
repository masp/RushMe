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

package com.tips48.rushMe.listeners;

import com.tips48.rushMe.GameManager;
import com.tips48.rushMe.RushMe;
import com.tips48.rushMe.arenas.Arena;
import com.tips48.rushMe.commands.RushMeCommand;
import com.tips48.rushMe.custom.GUI.MainHUD;
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.custom.events.PlayerFireGunEvent;
import com.tips48.rushMe.custom.items.GrenadeManager;
import com.tips48.rushMe.custom.items.Gun;
import com.tips48.rushMe.data.PlayerData;
import com.tips48.rushMe.gamemodes.GameModeType;
import com.tips48.rushMe.packets.PacketInfo;
import com.tips48.rushMe.teams.Team;
import com.tips48.rushMe.util.RMUtils;

import masp.tests.velocitytests.PlayerTestListener.CheckOnGround;
import net.minecraft.server.MathHelper;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;

import java.util.*;

public class RMPlayerListener extends PlayerListener {
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
	PacketInfo.onJoin(SpoutManager.getPlayer(event.getPlayer()));
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
	Player p = event.getPlayer();
	if (GameManager.inGame(p)) {
	    GameManager.getArenaOf(p).removePlayer(p);
	}
    }

    @Override
    public void onPlayerKick(PlayerKickEvent event) {
	Player p = event.getPlayer();
	if (GameManager.inGame(p)) {
	    GameManager.getArenaOf(p).removePlayer(p);
	}
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
	Player p = event.getPlayer();
	Action action = event.getAction();
	if ((action.equals(Action.RIGHT_CLICK_BLOCK) || action
		.equals(Action.RIGHT_CLICK_AIR))) {
	    if (RushMeCommand.isDefining(p)) {
		if (!(event.hasBlock())) {
		    return;
		}
		Arena a = RushMeCommand.getDefining(p);
		GameModeType type = a.getGameMode().getType();
		Vector vec = event.getClickedBlock().getLocation().toVector()
			.add(new Vector(0, 1, 0));
		if (a.getVector1() == null) {
		    a.setVector1(vec);
		    p.sendMessage(ChatColor.AQUA + "First point selected");
		} else if (a.getVector2() == null) {
		    a.setVector2(vec);
		    p.sendMessage(ChatColor.AQUA + "Second point selected");
		    if (type.equals(GameModeType.FLAG)) {
			p.sendMessage(ChatColor.AQUA
				+ "Right click to select the locations of flags");
			p.sendMessage(ChatColor.AQUA
				+ "Use the command /RushMe done <arena> when done");
		    } else if (type.equals(GameModeType.OBJECTIVE)) {
			p.sendMessage(ChatColor.AQUA
				+ "Right click to select the locations of the objectives");
			p.sendMessage(ChatColor.AQUA
				+ "Use the command /RushMe done <arena> when done");
		    } else if (type.equals(GameModeType.CAPTURE)) {
			p.sendMessage(ChatColor.AQUA
				+ "Right click to select the location of each of the capture points");
		    }
		} else if (type.equals(GameModeType.FLAG)) {
		    if (!a.inArena(vec)) {
			p.sendMessage(ChatColor.RED
				+ "Flag must be in the arena!");
			return;
		    }
		    a.addFlag(vec);
		    p.sendMessage(ChatColor.AQUA + "Flag selected");
		} else if (type.equals(GameModeType.OBJECTIVE)) {
		    if (!a.inArena(vec)) {
			p.sendMessage(ChatColor.RED
				+ "Objective must be in the arena!");
			return;
		    }
		    a.addObjective(vec);
		    p.sendMessage(ChatColor.AQUA + "Objective selected");
		} else if (type.equals(GameModeType.CAPTURE)) {
		    if (!a.inArena(vec)) {
			p.sendMessage(ChatColor.RED
				+ "Capture point must be in the arena!");
			return;
		    }
		    Team t = a.getTeams().get(0);
		    for (Team team : a.getCapturePoints().keySet()) {
			if (t == team) {
			    t = null;
			}
		    }
		    if (t == null) {
			t = a.getTeams().get(1);
		    }
		    a.addCapturePoint(t, vec);
		    p.sendMessage(ChatColor.AQUA + t.getName()
			    + "'s capture point was selected");
		    if (a.getCapturePoints().size() == a.getTeams().size()) {
			p.sendMessage(ChatColor.AQUA
				+ "Capture point selected for every team; Arena creation done.");
			RushMeCommand.remove(p);
		    }
		}
		event.setCancelled(true);
		return;
	    } else if (GrenadeManager.getSelectedGrenade(p).canBeUsed()) {
		GrenadeManager.getSelectedGrenade(p).fire(p);
	    } else if (RMUtils.isHoldingGun(p)) {
		event.setCancelled(true);
		return;
	    }
	}
	if (!GameManager.inGame(p)) {
	    return;
	}

	if (action.equals(Action.LEFT_CLICK_AIR)
		|| action.equals(Action.LEFT_CLICK_BLOCK)) {
	    if (RMUtils.isHoldingGun(p)) {
		Gun g = RMUtils.getGunOf(p);
		if (!(g.canFire())) {
		    event.setCancelled(true);
		    return;
		}
		PlayerFireGunEvent pfge = new PlayerFireGunEvent(p, g);
		Bukkit.getPluginManager().callEvent(pfge);
		if (pfge.isCancelled()) {
		    event.setCancelled(true);
		    return;
		}
		if (g.getBulletsExplode()) {
		    List<Block> lookedAt = p.getLineOfSight(null, 100);
		    Block last = lookedAt.get(lookedAt.size() - 1);
		    RMUtils.createAstheticExplosion(g, last.getLocation());
		    for (Entity e : RMUtils.getNearbyEntities(
			    last.getLocation(), g.getEntityDamageDistance(),
			    g.getEntityDamageDistance(),
			    g.getEntityDamageDistance())) {
			if (!(e instanceof LivingEntity)) {
			    return;
			}
			LivingEntity le = (LivingEntity) e;
			if (le instanceof Player) {
			    PlayerData.registerDamage((Player) le, p, 10, g);
			} else {
			    le.damage(10, p);
			}
		    }
		} else {
		    if (getLookingAtHead(p) != null) {
			LivingEntity e = getLookingAtHead(p);
			if (e instanceof Player) {
			    PlayerData.registerDamage((Player) e, p,
				    g.getHeadshotDamage(), g);
			} else {
			    e.damage(g.getHeadshotDamage(), p);
			}
		    } else if (getLookingAt(p) != null) {
			LivingEntity e = getLookingAt(p);
			if (e instanceof Player) {
			    PlayerData.registerDamage((Player) e, p,
				    g.getBodyDamage(), g);
			} else {
			    e.damage(g.getBodyDamage(), p);
			}
		    }
		}
		g.fire(p);
		event.setCancelled(true);
	    }
	}
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
	Player player = event.getPlayer();
	Location from = event.getFrom();
	if (GameManager.inGame(player)) {
	    Arena a = GameManager.getArenaOf(player);
	    if (a != null) {
		if (!(a.inArena(event.getTo().toVector()))) {
		    from.setX(from.getBlockX() + 0.5);
		    from.setY(from.getBlockY());
		    from.setZ(from.getBlockZ() + 0.5);
		    event.setTo(from);
		    return;
		}
	    }
	}
    }

    @Override
    public void onItemHeldChange(PlayerItemHeldEvent event) {
	final Player player = event.getPlayer();
	if (!GameManager.inGame(player)) {
	    return;
	}
	RushMe.getInstance().getServer().getScheduler()
		.scheduleSyncDelayedTask(RushMe.getInstance(), new Runnable() {
		    public void run() {
			if (RMUtils.isHoldingGun(player)) {
			    SpoutGUI.getHudOf(player).updateHUD();
			}
		    }
		}, 1);
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
	Player player = event.getPlayer();
	if (!GameManager.inGame(player)) {
	    return;
	}
	PlayerData.setHealth(player, 100);
	PlayerData.addDeath(player);
	MainHUD hud = SpoutGUI.getHudOf(player);
	if (hud != null) {
	    hud.updateHUD();
	}
    }

    private LivingEntity getLookingAtHead(Player player) {
	LivingEntity target = null;
	BlockIterator bitr = new BlockIterator(player, 100);
	Block b;
	Location l;
	int bx, by, bz;
	double ex, ey, ez;
	// loop through player's line of sight
	while (bitr.hasNext()) {
	    b = bitr.next();
	    bx = b.getX();
	    by = b.getY();
	    bz = b.getZ();
	    // check for entities near this block in the line of sight
	    Set<LivingEntity> entities = new HashSet<LivingEntity>();
	    for (Entity e : RMUtils.getNearbyEntities(new Location(
		    b.getWorld(), bx, by, bz), 3, 3, 3)) {
		if (e instanceof LivingEntity) {
		    entities.add((LivingEntity) e);
		}
	    }
	    for (LivingEntity e : entities) {
		l = e.getEyeLocation();
		ex = l.getX();
		ey = l.getY();
		ez = l.getZ();
		if ((((bx - .75) <= ex) && (ex <= (bx + 1.75)))
			&& (((bz - .75) <= ez) && (ez <= (bz + 1.75)))
			&& (((by - 1) <= ey) && (ey <= by))) {
		    if (target instanceof Player) {
			// Check teams
			if (GameManager.inGame((Player) target)) {
			    Arena a = GameManager.getArenaOf((Player) target);
			    if (a.getTeamOf((Player) target).equals(
				    a.getTeamOf(player))) {
				continue;
			    }
			}
		    }
		    // entity is close enough, set target and stop
		    target = e;
		    break;
		}
	    }
	}
	return target;
    }

    private LivingEntity getLookingAt(Player player) {
	LivingEntity target = null;
	BlockIterator bitr = new BlockIterator(player, 100);
	Block b;
	Location l;
	int bx, by, bz;
	double ex, ey, ez;
	// loop through player's line of sight
	while (bitr.hasNext()) {
	    b = bitr.next();
	    bx = b.getX();
	    by = b.getY();
	    bz = b.getZ();
	    // check for entities near this block in the line of sight
	    Set<LivingEntity> entities = new HashSet<LivingEntity>();
	    for (Entity e : RMUtils.getNearbyEntities(new Location(
		    b.getWorld(), bx, by, bz), 3, 3, 3)) {
		if (e instanceof LivingEntity) {
		    entities.add((LivingEntity) e);
		}
	    }
	    for (LivingEntity e : entities) {
		l = e.getLocation();
		ex = l.getX();
		ey = l.getY();
		ez = l.getZ();
		if ((((bx - .75) <= ex) && (ex <= (bx + 1.75)))
			&& (((bz - .75) <= ez) && (ez <= (bz + 1.75)))
			&& (((by - 1) <= ey) && (ey <= (by + 2.5)))) {
		    if (target instanceof Player) {
			if (GameManager.inGame((Player) target)) {
			    Arena a = GameManager.getArenaOf((Player) target);
			    if (a.getTeamOf((Player) target).equals(
				    a.getTeamOf(player))) {
				continue;
			    }
			}
		    }
		    // entity is close enough, set target and stop
		    target = e;
		    break;
		}
	    }
	}
	return target;
    }
}
