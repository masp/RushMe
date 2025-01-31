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

package com.tips48.rushMe.teams;

import com.tips48.rushMe.GameManager;
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.data.PlayerData;
import com.tips48.rushMe.packets.PacketInfo;
import com.tips48.rushMe.packets.PacketTeamUpdate;
import com.tips48.rushMe.util.RMUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class Team extends PacketInfo {
    private final TIntSet players;
    private int spawnsLeft;
    private final String name;
    private final int playerLimit;
    private List<Location> spawns = new ArrayList<Location>();
    private boolean infiniteSpawns;

    private final String prefix;
    private final String skin;
    private final Integer maxSpawnsLeft;

    private final UUID uuid;
    private UUID ownerUUID;

    public Team(String name, String prefix, int playerLimit, String skin,
	    Integer maxSpawnsLeft, boolean infiniteSpawns, UUID ownerUUID,
	    UUID uuid) {
	this.name = name;
	this.playerLimit = playerLimit;
	this.prefix = prefix;
	this.skin = skin;
	this.infiniteSpawns = infiniteSpawns;
	this.maxSpawnsLeft = maxSpawnsLeft;
	spawnsLeft = maxSpawnsLeft;

	players = new TIntHashSet();

	if (ownerUUID != null) {
	    this.ownerUUID = ownerUUID;
	}

	if (uuid != null) {
	    this.uuid = uuid;
	} else {
	    this.uuid = UUID.randomUUID();
	}
    }

    public String getName() {
	return name;
    }

    public String getPrefix() {
	return prefix;
    }

    public int getSpawnsLeft() {
	return spawnsLeft;
    }

    public void addSpawnsLeft() {
	spawnsLeft++;
	for (int player : players.toArray()) {
	    Player p = SpoutManager.getPlayerFromId(player);
	    if (p != null) {
		SpoutGUI.getHudOf(p).updateHUD();
	    }
	}
	PacketTeamUpdate packet = new PacketTeamUpdate();
	packet.processTeam(this);
	packet.send(RMUtils.getSpoutPlayers());
    }

    public void subtractSpawnsLeft() {
	spawnsLeft--;
	for (int player : players.toArray()) {
	    Player p = SpoutManager.getPlayerFromId(player);
	    if (p != null) {
		SpoutGUI.getHudOf(p).updateHUD();
	    }
	}
	PacketTeamUpdate packet = new PacketTeamUpdate();
	packet.processTeam(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public void setSpawnsLeft(int spawnsLeft) {
	this.spawnsLeft = spawnsLeft;
	for (int player : players.toArray()) {
	    Player p = SpoutManager.getPlayerFromId(player);
	    if (p != null) {
		SpoutGUI.getHudOf(p).updateHUD();
	    }
	}
	PacketTeamUpdate packet = new PacketTeamUpdate();
	packet.processTeam(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public TIntSet getPlayers() {
	return players;
    }

    public boolean addPlayer(int player) {
	if (playerLimit > players.size()) {
	    players.add(player);
	    PacketTeamUpdate packet = new PacketTeamUpdate();
	    packet.processTeam(this);
	    packet.send(RMUtils.getSpoutPlayers());
	    PacketInfo.addPacket(packet, packet);
	    return true;
	}
	return false;
    }

    public void removePlayer(int player) {
	if (players.contains(player)) {
	    players.remove(player);
	    PacketTeamUpdate packet = new PacketTeamUpdate();
	    packet.processTeam(this);
	    packet.send(RMUtils.getSpoutPlayers());
	    PacketInfo.addPacket(packet, packet);
	}
    }

    public boolean addPlayer(Player player) {
	return addPlayer(player.getEntityId());
    }

    public void removePlayer(Player player) {
	removePlayer(player.getEntityId());
    }

    public boolean containsPlayer(int player) {
	return players.contains(player);
    }

    public boolean containsPlayer(Player player) {
	return containsPlayer(player.getEntityId());
    }

    public int getPlayerLimit() {
	return playerLimit;
    }

    public List<Location> getSpawns() {
	return spawns;
    }

    public void setSpawns(List<Location> spawns) {
	this.spawns = spawns;
	PacketTeamUpdate packet = new PacketTeamUpdate();
	packet.processTeam(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public void addSpawn(Location spawn) {
	spawns.add(spawn);
	PacketTeamUpdate packet = new PacketTeamUpdate();
	packet.processTeam(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public boolean getInfiniteSpawns() {
	return infiniteSpawns;
    }

    public void setInfiniteSpawns(boolean infiniteSpawns) {
	this.infiniteSpawns = infiniteSpawns;
	PacketTeamUpdate packet = new PacketTeamUpdate();
	packet.processTeam(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public TIntList getByScore() {
	TIntList byScore = new TIntArrayList();
	int playersOnTeam = players.size();
	Integer[] scores = new Integer[playersOnTeam];
	for (int i = 0; i < playersOnTeam; i++) {
	    if (i > 0) {
		scores[i] = getHighestScore(scores[i - 1]);
	    } else {
		scores[i] = getHighestScore();
	    }
	}
	for (int i = 0; i < playersOnTeam; i++) {
	    for (int name : PlayerData.getScores().keySet().toArray()) {
		if (!players.contains(name)) {
		    continue;
		}
		if (!GameManager.inGame(name)) {
		    continue;
		}
		if (scores[i] == PlayerData.getScore(name)) {
		    if (!byScore.contains(name)) {
			byScore.insert(i, name);
		    }
		}
	    }
	}

	return byScore;
    }

    private int getHighestScore(int lastScore) {
	int otherScore = 0;
	for (int score : PlayerData.getScores().values()) {
	    if (score < lastScore) {
		if (score > otherScore) {
		    otherScore = score;
		}
	    }
	}
	return otherScore;
    }

    private int getHighestScore() {
	int highest = 0;
	for (int score : PlayerData.getScores().values()) {
	    if (score > highest) {
		highest = score;
	    }
	}
	return highest;
    }

    public void doWon() {
	// TODO
    }

    public void doLost() {
	// TODO
    }

    public String getSkin() {
	return skin;
    }

    public int getMaxSpawnsLeft() {
	return maxSpawnsLeft;
    }

    public UUID getUUID() {
	return uuid;
    }

    public UUID getOwnerUUID() {
	return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
	this.ownerUUID = ownerUUID;
	PacketTeamUpdate packet = new PacketTeamUpdate();
	packet.processTeam(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public int checkStatus() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reset() {
	spawnsLeft = maxSpawnsLeft;
	// TODO reset spawns
    }

    @Override
    public String toString() {
	return name;
    }
}
