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

package com.tips48.rushMe.custom.GUI;

import com.tips48.rushMe.packets.*;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MainHUD {

	private SpoutPlayer player;
	private boolean active;

	public MainHUD(Player player) {
		this.player = SpoutManager.getPlayer(player);
		active = false;
	}

	public void init() {
		PacketToggleHUD packet = new PacketToggleHUD();
		packet.send(player);
		active = true;
	}

	public void shutdown() {
		PacketToggleHUD packet = new PacketToggleHUD();
		packet.send(player);
		active = false;
	}

	public void updateHUD() {
		PacketUpdateHUD packet = new PacketUpdateHUD();
		packet.send(player);
	}

	public void showHit() {
		PacketShowHit packet = new PacketShowHit();
		packet.send(player);
	}

	public boolean isActive() {
		return active;
	}

	public void queuePoints(String message) {
		PacketQueuePoints packet = new PacketQueuePoints();
		packet.setMessage(message);
		packet.send(player);
	}

	public void queueKill(String message) {
		PacketQueueKill packet = new PacketQueueKill();
		packet.setMessage(message);
		packet.send(player);
	}

}
