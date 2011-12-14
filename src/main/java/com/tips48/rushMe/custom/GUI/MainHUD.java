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

    private final int player;
    private boolean active;

    public MainHUD(Player player) {
	this.player = player.getEntityId();
	active = false;
    }

    public void init() {
	if (active) {
	    return;
	}
	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p == null) {
	    return;
	}
	PacketToggleHUD packet = new PacketToggleHUD();
	packet.send(p);
	active = true;
    }

    public void shutdown() {
	if (!(active)) {
	    return;
	}
	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p == null) {
	    return;
	}
	PacketToggleHUD packet = new PacketToggleHUD();
	packet.send(p);
	active = false;
    }

    public void updateHUD() {
	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p == null) {
	    return;
	}
	PacketUpdateHUD packet = new PacketUpdateHUD();
	packet.send(p);
    }

    public void showHit() {
	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p == null) {
	    return;
	}
	PacketShowHit packet = new PacketShowHit();
	packet.send(p);
    }

    public void doConcussion(int startingAlpha, int time) {
	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p == null) {
	    return;
	}
	PacketDoConcussion packet = new PacketDoConcussion();
	packet.setStartingAlpha(startingAlpha);
	packet.setTime(time);
	packet.send(p);
    }

    public boolean isActive() {
	return active;
    }

    public void queuePoints(String message) {
	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p == null) {
	    return;
	}
	PacketQueuePoints packet = new PacketQueuePoints();
	packet.setMessage(message);
	packet.send(p);
    }

    public void queueKill(String message) {
	SpoutPlayer p = SpoutManager.getPlayerFromId(player);
	if (p == null) {
	    return;
	}
	PacketQueueKill packet = new PacketQueueKill();
	packet.setMessage(message);
	packet.send(p);
    }

}
