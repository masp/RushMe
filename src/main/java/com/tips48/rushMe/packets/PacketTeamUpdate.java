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

package com.tips48.rushMe.packets;

import com.tips48.rushMe.GameManager;
import com.tips48.rushMe.arenas.Arena;
import com.tips48.rushMe.gamemodes.GameMode;
import com.tips48.rushMe.teams.Team;
import com.tips48.rushMe.util.RMLogging;

import org.bukkit.Location;
import org.getspout.spoutapi.io.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;
import java.util.logging.Level;

public class PacketTeamUpdate extends AddonPacket implements PriorityPacket {

	private TIntSet players = new TIntHashSet();
	private int spawnsLeft;
	private String name;
	private int playerLimit;
	private List<Location> spawns = new ArrayList<Location>();
	private boolean infiniteSpawns;
	private String prefix;
	private String skin;
	private Integer maxSpawnsLeft;

	private UUID uuid;
	private UUID ownerUUID;

	@Override
	public void read(SpoutInputStream stream) {
		name = stream.readString();
		prefix = stream.readString();
		skin = stream.readString();
		infiniteSpawns = stream.readInt() == 0;
		playerLimit = stream.readInt();
		spawnsLeft = stream.readInt();
		maxSpawnsLeft = stream.readInt();
		uuid = UUID.fromString(stream.readString());
		ownerUUID = UUID.fromString(stream.readString());
		int spawnsLength = stream.readInt();
		for (int i = 0; i < spawnsLength; i++) {
			spawns.add(stream.readLocation());
		}
		int playersLength = stream.readInt();
		for (int i = 0; i < playersLength; i++) {
			players.add(stream.readInt());
		}
	}

	@Override
	public void run(SpoutPlayer sp) {
		Team team = new Team(name, prefix, playerLimit, skin, maxSpawnsLeft,
				infiniteSpawns, ownerUUID, uuid);
		for (int i : players.toArray()) {
			team.addPlayer(i);
		}
		for (Location l : spawns) {
			team.addSpawn(l);
		}
		team.setSpawnsLeft(spawnsLeft);

		GameMode gm = GameManager.getGameMode(ownerUUID);
		if (gm != null) {
			gm.replaceTeam(team);
			return;
		}
		Arena a = GameManager.getArena(ownerUUID);
		if (a != null) {
			a.replaceTeam(team);
			return;
		}
		RMLogging.log(Level.SEVERE, "PacketTeamUpdate sent wrongly!");
	}

	@Override
	public void write(SpoutOutputStream stream) {
		stream.writeString(name);
		stream.writeString(prefix);
		stream.writeString(skin);
		stream.writeInt(infiniteSpawns == true ? 0 : 1);
		stream.writeInt(playerLimit);
		stream.writeInt(spawnsLeft);
		stream.writeInt(maxSpawnsLeft);
		stream.writeString(uuid.toString());
		stream.writeString(ownerUUID.toString());
		stream.writeInt(spawns.size());
		for (Location loc : spawns) {
			stream.writeLocation(loc);
		}
		stream.writeInt(players.size());
		for (int i : players.toArray()) {
			stream.writeInt(i);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public boolean isInfiniteSpawns() {
		return infiniteSpawns;
	}

	public void setInfiniteSpawns(boolean infiniteSpawns) {
		this.infiniteSpawns = infiniteSpawns;
	}

	public int getPlayerLimit() {
		return playerLimit;
	}

	public void setPlayerLimit(int playerLimit) {
		this.playerLimit = playerLimit;
	}

	public int getSpawnsLeft() {
		return spawnsLeft;
	}

	public void setSpawnsLeft(int spawnsLeft) {
		this.spawnsLeft = spawnsLeft;
	}

	public Integer getMaxSpawnsLeft() {
		return maxSpawnsLeft;
	}

	public void setMaxSpawnsLeft(Integer maxSpawnsLeft) {
		this.maxSpawnsLeft = maxSpawnsLeft;
	}

	public List<Location> getSpawns() {
		return spawns;
	}

	public void setSpawns(List<Location> spawns) {
		this.spawns = spawns;
	}

	public TIntSet getPlayers() {
		return players;
	}

	public void setPlayers(TIntSet players) {
		this.players = players;
	}

	public UUID getOwnerUUID() {
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public PacketTeamUpdate processTeam(Team team) {
		setName(team.getName());
		setPrefix(team.getPrefix());
		setSkin(team.getSkin());
		setSpawnsLeft(team.getSpawnsLeft());
		setMaxSpawnsLeft(team.getMaxSpawnsLeft());
		setSpawns(team.getSpawns());
		setInfiniteSpawns(team.getInfiniteSpawns());
		setPlayerLimit(team.getPlayerLimit());
		setPlayers(team.getPlayers());
		setUUID(team.getUUID());
		setOwnerUUID(team.getOwnerUUID());
		return this;
	}

	@Override
	public PacketPriority getPriority() {
		return PacketPriority.HIGH;
	}

}
