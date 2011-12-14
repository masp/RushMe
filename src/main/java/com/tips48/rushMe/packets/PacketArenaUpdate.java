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
import com.tips48.rushMe.RushMe;
import com.tips48.rushMe.arenas.Arena;
import com.tips48.rushMe.teams.Team;

import org.bukkit.util.Vector;
import org.getspout.spoutapi.io.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class PacketArenaUpdate extends AddonPacket implements PriorityPacket {

	private String name;
	private TIntSet players = new TIntHashSet();
	private boolean started;
	private Vector vec1;
	private Vector vec2;
	private int creator;
	private List<Vector> flagLocations = new ArrayList<Vector>();
	private Map<UUID, Vector> captureLocations = new HashMap<UUID, Vector>();
	private List<Vector> objectiveLocations = new ArrayList<Vector>();
	private List<Vector> activeObjectiveLocations = new ArrayList<Vector>();
	private UUID uuid;
	private UUID gamemodeUUID;
	private UUID worldUUID;

	@Override
	public void read(SpoutInputStream stream) {
		name = stream.readString();
		creator = stream.readInt();
		uuid = stream.readUUID();
		gamemodeUUID = stream.readUUID();
		worldUUID = stream.readUUID();
		int vectorsNotNull = stream.readInt();
		if (vectorsNotNull >= 2) {
			vec1 = stream.readVector();
			vec2 = stream.readVector();
		} else if (vectorsNotNull == 1) {
			vec1 = stream.readVector();
		}
		started = stream.readInt() == 0;
		int playersLength = stream.readInt();
		for (int i = 0; i < playersLength; i++) {
			players.add(stream.readInt());
		}
		int flagLocationsLength = stream.readInt();
		for (int i = 0; i < flagLocationsLength; i++) {
			flagLocations.add(stream.readVector());
		}
		int captureLocationsLength = stream.readInt();
		for (int i = 0; i < captureLocationsLength; i++) {
			captureLocations.put(stream.readUUID(), stream.readVector());
		}
		int objectLocationsLength = stream.readInt();
		for (int i = 0; i < objectLocationsLength; i++) {
			objectiveLocations.add(stream.readVector());
		}
		int activeObjectiveLocationsLength = stream.readInt();
		for (int i = 0; i < activeObjectiveLocationsLength; i++) {
			activeObjectiveLocations.add(stream.readVector());
		}
	}

	@Override
	public void run(SpoutPlayer sp) {
		Arena arena = GameManager.getArena(uuid);
		if (arena == null) {
			arena = GameManager.createArena(name,
					GameManager.getGameMode(gamemodeUUID), creator, RushMe
							.getInstance().getServer().getWorld(worldUUID),
					uuid);
		}
		if (vec1 != null) {
			arena.setVector1(vec1);
		}
		if (vec2 != null) {
			arena.setVector2(vec2);
		}
		for (Vector flag : flagLocations) {
			arena.addFlag(flag);
		}
		for (UUID uuid : captureLocations.keySet()) {
			arena.addCapturePoint(arena.getTeam(uuid),
					captureLocations.get(uuid));
		}
		for (Vector objective : objectiveLocations) {
			arena.addObjective(objective);
		}
		for (Vector activeObjective : activeObjectiveLocations) {
			arena.addActiveObjective(activeObjective);
		}
		for (int i : players.toArray()) {
			arena.addPlayer(i, null);
		}
		if (arena.isStarted() && !started) {
			arena.stop();
		} else if (!arena.isStarted() && started) {
			arena.start();
		}
	}

	@Override
	public void write(SpoutOutputStream stream) {
		stream.writeString(name);
		stream.writeInt(creator);
		stream.writeUUID(uuid);
		stream.writeUUID(gamemodeUUID);
		stream.writeUUID(worldUUID);
		if (vec1 != null && vec2 != null) {
			stream.writeInt(2);
			stream.writeVector(vec1);
			stream.writeVector(vec2);
		} else if (vec1 != null) {
			stream.writeInt(1);
			stream.writeVector(vec1);
		} else {
			stream.writeInt(0);
		}
		stream.writeInt(started == true ? 0 : 1);
		if (players != null) {
			stream.writeInt(players.size());
			for (int i : players.toArray()) {
				stream.writeInt(i);
			}
		} else {
			stream.writeInt(0);
		}
		if (flagLocations != null) {
			stream.writeInt(flagLocations.size());
			for (Vector v : flagLocations) {
				stream.writeVector(v);
			}
		} else {
			stream.writeInt(0);
		}
		if (captureLocations != null) {
			stream.writeInt(captureLocations.size());
			for (UUID uuid : captureLocations.keySet()) {
				stream.writeUUID(uuid);
				stream.writeVector(captureLocations.get(uuid));
			}
		} else {
			stream.writeInt(0);
		}
		if (objectiveLocations != null) {
			stream.writeInt(objectiveLocations.size());
			for (Vector v : objectiveLocations) {
				stream.writeVector(v);
			}
		} else {
			stream.writeInt(0);
		}
		if (activeObjectiveLocations != null) {
			stream.writeInt(activeObjectiveLocations.size());
			for (Vector v : activeObjectiveLocations) {
				stream.writeVector(v);
			}
		} else {
			stream.writeInt(0);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public Vector getVec1() {
		return vec1;
	}

	public void setVec1(Vector vec1) {
		this.vec1 = vec1;
	}

	public Vector getVec2() {
		return vec2;
	}

	public void setVec2(Vector vec2) {
		this.vec2 = vec2;
	}

	public int getCreator() {
		return creator;
	}

	public void setCreator(int creator) {
		this.creator = creator;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getGamemodeUUID() {
		return gamemodeUUID;
	}

	public void setGamemodeUUID(UUID gamemodeUUID) {
		this.gamemodeUUID = gamemodeUUID;
	}

	public UUID getWorldUUID() {
		return worldUUID;
	}

	public void setWorldUUID(UUID worldUUID) {
		this.worldUUID = worldUUID;
	}

	public List<Vector> getActiveObjectiveLocations() {
		return activeObjectiveLocations;
	}

	public void setActiveObjectiveLocations(
			List<Vector> activeObjectiveLocations) {
		this.activeObjectiveLocations = activeObjectiveLocations;
	}

	public List<Vector> getFlagLocations() {
		return flagLocations;
	}

	public void setFlagLocations(List<Vector> flagLocations) {
		this.flagLocations = flagLocations;
	}

	public Map<UUID, Vector> getCaptureLocations() {
		return captureLocations;
	}

	public void setCaptureLocations(Map<UUID, Vector> captureLocations) {
		this.captureLocations = captureLocations;
	}

	public List<Vector> getObjectiveLocations() {
		return objectiveLocations;
	}

	public void setObjectiveLocations(List<Vector> objectLocations) {
		this.objectiveLocations = objectLocations;
	}

	public TIntSet getPlayers() {
		return players;
	}

	public void setPlayers(TIntSet players) {
		this.players = players;
	}

	public PacketArenaUpdate processArena(Arena arena) {
		setName(arena.getName());
		setStarted(arena.isStarted());
		setVec1(arena.getVector1());
		setVec2(arena.getVector2());
		setCreator(arena.getCreator());
		setUUID(arena.getUUID());
		setGamemodeUUID(arena.getGameMode().getUUID());
		setWorldUUID(arena.getWorld().getUID());
		setActiveObjectiveLocations(arena.getActiveObjectives());
		setObjectiveLocations(arena.getObjectives());
		setFlagLocations(arena.getFlags());
		Map<Team, Vector> captureLocations = arena.getCapturePoints();
		Map<UUID, Vector> tempCaptureLocations = new HashMap<UUID, Vector>(
				captureLocations.size());
		for (Team team : captureLocations.keySet()) {
			tempCaptureLocations
					.put(team.getUUID(), captureLocations.get(team));
		}
		setPlayers(arena.getPlayers());
		return this;
	}

	@Override
	public PacketPriority getPriority() {
		return PacketPriority.NORMAL;
	}

}
