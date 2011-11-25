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

package com.tips48.rushMe.data;

import com.tips48.rushMe.GameManager;
import com.tips48.rushMe.custom.GUI.MainHUD;
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.custom.items.Gun;
import com.tips48.rushMe.packets.PacketPlayerDataUpdate;
import com.tips48.rushMe.util.RMUtils;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class PlayerData {
	private static final TIntIntMap scores = new TIntIntHashMap();
	private static final TIntIntMap kills = new TIntIntHashMap();
	private static final TIntIntMap deaths = new TIntIntHashMap();
	private static final TIntIntMap health = new TIntIntHashMap();
	private static final TIntSet spotted = new TIntHashSet();

	private PlayerData() {

	}

	public static void registerDamage(Player hurt, Player damager, int damage,
			Gun gun) {
		registerDamage(hurt.getEntityId(), damager.getEntityId(), damage, gun);
	}

	public static void registerDamage(int hurt, int damager, int damage, Gun gun) {
		Player hurtP = SpoutManager.getPlayerFromId(hurt);
		Player damagerP = SpoutManager.getPlayerFromId(damager);

		if ((hurtP == null) || (damagerP == null)) {
			setHealth(hurt, damage);
			return;
		}

		MainHUD hurtHud = SpoutGUI.getHudOf(hurtP);
		MainHUD damagerHud = SpoutGUI.getHudOf(damagerP);

		if (!(GameManager.inGame(hurtP))) {
			return;
		}

		setHealth(hurt, damage);

		if ((hurtHud != null) && hurtHud.isActive()) {
			hurtHud.updateHUD();
		}

		if ((damagerHud != null) && damagerHud.isActive()) {
			damagerHud.updateHUD();
			damagerHud.showHit();
		}

		if (getHealth(hurt) <= 0) {
			addDeath(damager);
			SpoutGUI.showKill(damagerP, hurtP, gun);
			damagerHud.queuePoints("Enemy killed - 100");
			setScore(damagerP, getScore(damagerP) + 100);
		}
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(hurtP));
		packet.setKills(PlayerData.getKills(hurtP));
		packet.setScore(PlayerData.getScore(hurtP));
		packet.setSpotted(PlayerData.isSpotted(hurtP));
		packet.send(RMUtils.getSpoutPlayers());

		PacketPlayerDataUpdate packet2 = new PacketPlayerDataUpdate();
		packet2.setDeaths(PlayerData.getDeaths(damagerP));
		packet2.setKills(PlayerData.getKills(damagerP));
		packet2.setScore(PlayerData.getScore(damagerP));
		packet2.setSpotted(PlayerData.isSpotted(damagerP));
		packet2.send(RMUtils.getSpoutPlayers());
		// TODO if keeping gun stats, do here
	}

	public static int getScore(Player player) {
		return getScore(player.getEntityId());
	}

	public static int getScore(int player) {
		return scores.get(player);

	}

	public static TIntIntMap getScores() {
		return scores;
	}

	public static void setScore(Player player, Integer score) {
		setScore(player.getEntityId(), score);
	}

	public static void setScore(int player, Integer score) {
		scores.put(player, score);
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static int getKills(Player player) {
		return getKills(player.getEntityId());
	}

	public static int getKills(int player) {
		return kills.get(player);
	}

	public static void setKills(Player player, Integer kill) {
		setKills(player.getEntityId(), kill);

	}

	public static void setKills(int player, Integer kill) {
		kills.put(player, kill);
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static int getDeaths(Player player) {
		return getDeaths(player.getEntityId());
	}

	public static int getDeaths(int player) {
		return deaths.get(player);
	}

	public static void setDeaths(Player player, Integer death) {
		setDeaths(player.getEntityId(), death);
	}

	public static void setDeaths(int player, Integer death) {
		deaths.put(player, death);
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static void addKill(Player player) {
		addKill(player.getEntityId());
	}

	public static void addKill(int player) {
		kills.put(player, kills.get(player) + 1);
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static void addDeath(Player player) {
		addDeath(player.getEntityId());
	}

	public static void addDeath(int player) {
		deaths.put(player, deaths.get(player) + 1);
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static int getHealth(Player player) {
		return getHealth(player.getEntityId());
	}

	public static int getHealth(int player) {
		return health.get(player);
	}

	public static void setHealth(Player player, Integer h) {
		setHealth(player.getEntityId(), h);
	}

	public static void setHealth(int player, Integer h) {
		health.put(player, h);
		Player p = SpoutManager.getPlayerFromId(player);
		if (p != null) {
			MainHUD hud = SpoutGUI.getHudOf(p);
			if (hud != null) {
				hud.updateHUD();
			}
		}
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static void damage(Player player, Integer h) {
		damage(player.getEntityId(), h);
	}

	public static void damage(int player, Integer h) {
		int pHealth = health.get(player);
		if ((pHealth - h) >= 0) {
			health.put(player, pHealth - h);
		} else {
			health.put(player, 0);
		}
		Player p = SpoutManager.getPlayerFromId(player);
		if (p != null) {
			MainHUD hud = SpoutGUI.getHudOf(p);
			if (hud != null) {
				hud.updateHUD();
			}
		}
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static void heal(Player player, Integer h) {
		heal(player.getEntityId(), h);
	}

	public static void heal(int player, Integer h) {
		int pHealth = health.get(player);
		if ((pHealth + h) <= 100) {
			health.put(player, pHealth + h);
		} else {
			health.put(player, 100);
		}
		Player p = SpoutManager.getPlayerFromId(player);
		if (p != null) {
			MainHUD hud = SpoutGUI.getHudOf(p);
			if (hud != null) {
				hud.updateHUD();
			}
		}
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static void setDefaults(Player player) {
		setDefaults(player.getEntityId());
	}

	public static void setDefaults(int player) {
		setDeaths(player, 0);
		setHealth(player, 100);
		setKills(player, 0);
		setScore(player, 0);
	}

	public static void setSpotted(Player player, boolean s) {
		setSpotted(player.getEntityId(), s);
	}

	public static void setSpotted(int player, boolean s) {
		if (s) {
			spotted.add(player);
		} else {
			if (spotted.contains(player)) {
				spotted.remove(player);
			}
		}
		PacketPlayerDataUpdate packet = new PacketPlayerDataUpdate();
		packet.setDeaths(PlayerData.getDeaths(player));
		packet.setKills(PlayerData.getKills(player));
		packet.setScore(PlayerData.getScore(player));
		packet.setSpotted(PlayerData.isSpotted(player));
		packet.send(RMUtils.getSpoutPlayers());
	}

	public static boolean isSpotted(Player player) {
		return isSpotted(player.getEntityId());
	}

	public static boolean isSpotted(int player) {
		return spotted.contains(player);
	}

}
