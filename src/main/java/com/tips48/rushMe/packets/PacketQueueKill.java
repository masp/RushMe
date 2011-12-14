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

public class PacketQueueKill extends AddonPacket {
	private String message;

	@Override
	public void read(SpoutInputStream stream) {
		message = stream.readString();
		RMLogging.debugLog(Level.INFO, "Read PacketQueueKill with message: "
				+ message);
	}

	@Override
	public void run(SpoutPlayer sp) {
		RMLogging.debugLog(Level.INFO,
				"Running PacketQueueKill for " + sp.getName());
		MainHUD hud = SpoutGUI.getHudOf(sp);
		if (hud != null) {
			hud.queueKill(message);
		}
	}

	@Override
	public void write(SpoutOutputStream stream) {
		stream.writeString(message);
		RMLogging.debugLog(Level.INFO, "Wrote PacketQueueKill with message: "
				+ message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
