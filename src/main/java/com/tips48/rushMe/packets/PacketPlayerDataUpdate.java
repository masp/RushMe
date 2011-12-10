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

import com.tips48.rushMe.data.PlayerData;
import com.tips48.rushMe.util.RMLogging;

import org.getspout.spoutapi.io.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.logging.Level;

public class PacketPlayerDataUpdate extends AddonPacket {

	private int player;
	private int score;
	private int kills;
	private int deaths;
	private int health;
	private boolean spotted;

	@Override
	public void read(SpoutInputStream stream) {
		player = stream.readInt();
		score = stream.readInt();
		kills = stream.readInt();
		deaths = stream.readInt();
		health = stream.readInt();
		spotted = stream.readInt() == 0;
		RMLogging.debugLog(Level.INFO,
				"Read PacketPlayerDataUpdate.  Atributes:");
		RMLogging.debugLog(Level.INFO, "Score = " + score + ";Kills = " + kills
				+ ";Deaths = " + deaths + ";Healths = " + health
				+ ";Spotted = " + spotted);
	}

	@Override
	public void run(SpoutPlayer sp) {
		RMLogging.debugLog(Level.INFO, "Running PacketPlayerDataUpdate for "
				+ sp.getName());
		PlayerData.setScore(player, score);
		PlayerData.setKills(player, kills);
		PlayerData.setDeaths(player, deaths);
		PlayerData.setHealth(player, health);
		PlayerData.setSpotted(player, spotted);
	}

	@Override
	public void write(SpoutOutputStream stream) {
		stream.writeInt(player);
		stream.writeInt(score);
		stream.writeInt(kills);
		stream.writeInt(deaths);
		stream.writeInt(health);
		stream.writeInt(spotted ? 0 : 1);
		RMLogging.debugLog(Level.INFO,
				"Wrote PacketPlayerDataUpdate.  Atributes:");
		RMLogging.debugLog(Level.INFO, "Score = " + score + ";Kills = " + kills
				+ ";Deaths = " + deaths + ";Healths = " + health
				+ ";Spotted = " + spotted);
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isSpotted() {
		return spotted;
	}

	public void setSpotted(boolean spotted) {
		this.spotted = spotted;
	}

}
