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

import org.getspout.spoutapi.io.AddonPacket;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.*;

public class PacketInfo {

	private static Map<PriorityPacket, AddonPacket> toSend = new HashMap<PriorityPacket, AddonPacket>();

	public synchronized static void addPacket(PriorityPacket packet,
			AddonPacket ap) {
		Class<?> clazz = packet.getClass();
		Iterator<PriorityPacket> it = toSend.keySet().iterator();
		while (it.hasNext()) {
			PriorityPacket pp = it.next();
			if (pp.getClass().getName().equals(clazz.getName())) {
				it.remove();
			}
		}
		toSend.put(packet, ap);
	}

	public synchronized static void remove(PriorityPacket packet) {
		if (toSend.containsKey(packet)) {
			toSend.remove(packet);
		}
	}

	public synchronized static void onJoin(SpoutPlayer player) {
		for (PacketPriority priority : PacketPriority.values()) {
			for (PriorityPacket packet : toSend.keySet()) {
				if (packet.getPriority().equals(priority)) {
					toSend.get(packet).send(player);
				}
			}
		}
		PlayerData.sendPacketsOnJoin(player);
	}
}
