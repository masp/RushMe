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

import com.tips48.rushMe.RushMe;
import com.tips48.rushMe.GameManager;
import com.tips48.rushMe.gamemodes.GameMode;
import com.tips48.rushMe.gamemodes.GameModeType;

import org.getspout.spoutapi.io.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.UUID;
import java.util.logging.Level;

public class PacketGameModeCreate extends AddonPacket implements PriorityPacket {

    private String name;
    private int time;
    private GameModeType type;
    private boolean respawn;
    private int respawnTime;
    private int maxPlayers;
    private UUID uuid;

    @Override
    public void read(SpoutInputStream stream) {
	name = stream.readString();
	time = stream.readInt();
	int typeInt = stream.readInt();
	try {
	    type = GameModeType.getByCode(typeInt);
	} catch (Exception e) {
	    RushMe.getInstance().getLogger()
		    .log(e, "PacketGameModeCreate was sent wrongly!");
	}
	respawn = stream.readInt() == 0;
	respawnTime = stream.readInt();
	maxPlayers = stream.readInt();
	uuid = stream.readUUID();
    }

    @Override
    public void run(SpoutPlayer sp) {
	GameMode mode = GameManager.getGameMode(uuid);
	if (mode == null) {
	    GameManager.createGameMode(name, type, time, respawn, respawnTime,
		    maxPlayers, null, uuid);
	} else {
	    RushMe.getInstance()
		    .getLogger()
		    .log(Level.SEVERE, "PacketGameModeCreate was sent wrongly!");
	}
    }

    @Override
    public void write(SpoutOutputStream stream) {
	stream.writeString(name);
	stream.writeInt(time);
	stream.writeInt(type.getCode());
	stream.writeInt(respawn == true ? 0 : 1);
	stream.writeInt(respawnTime);
	stream.writeInt(maxPlayers);
	stream.writeUUID(uuid);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getTime() {
	return time;
    }

    public void setTime(int time) {
	this.time = time;
    }

    public GameModeType getType() {
	return type;
    }

    public void setType(GameModeType type) {
	this.type = type;
    }

    public boolean shouldRespawn() {
	return respawn;
    }

    public void setRespawn(boolean respawn) {
	this.respawn = respawn;
    }

    public int getRespawnTime() {
	return respawnTime;
    }

    public void setRespawnTime(int respawnTime) {
	this.respawnTime = respawnTime;
    }

    public int getMaxPlayers() {
	return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
	this.maxPlayers = maxPlayers;
    }

    public UUID getUUID() {
	return uuid;
    }

    public void setUUID(UUID uuid) {
	this.uuid = uuid;
    }

    public PacketGameModeCreate processGameMode(GameMode gamemode) {
	setName(gamemode.getName());
	setTime(gamemode.getTime());
	setType(gamemode.getType());
	setRespawn(gamemode.shouldRespawn());
	setMaxPlayers(gamemode.getMaxPlayers());
	setUUID(gamemode.getUUID());
	return this;
    }

    @Override
    public PacketPriority getPriority() {
	return PacketPriority.HIGHEST;
    }
}
