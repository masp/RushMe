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

import com.tips48.rushMe.custom.GUI.MainHUD;
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.util.RMLogging;

import org.getspout.spoutapi.io.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.logging.Level;

public class PacketDoConcussion extends AddonPacket implements PriorityPacket {

	private int startingAlpha;
	private int time;

	@Override
	public void read(SpoutInputStream stream) {
		startingAlpha = stream.readInt();
		time = stream.readInt();
		RMLogging.debugLog(Level.INFO,
				"Read PacketDoConcussion with startingAlpha = " + startingAlpha
						+ " and time = " + time);
	}

	@Override
	public void run(SpoutPlayer sp) {
		RMLogging.debugLog(Level.INFO,
				"Running PacketDoConcussion for " + sp.getName());
		MainHUD hud = SpoutGUI.getHudOf(sp);
		if (hud != null) {
			hud.doConcussion(startingAlpha, time);
		}
	}

	@Override
	public void write(SpoutOutputStream stream) {
		stream.writeInt(startingAlpha);
		stream.writeInt(time);
		RMLogging.debugLog(Level.INFO,
				"Wrote PacketDoConcussion with startingAlpha = "
						+ startingAlpha + " and time = " + time);
	}

	public int getStartingAlpha() {
		return startingAlpha;
	}

	public void setStartingAlpha(int startingAlpha) {
		this.startingAlpha = startingAlpha;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public PacketPriority getPriority() {
		return PacketPriority.LOWEST;
	}

}
