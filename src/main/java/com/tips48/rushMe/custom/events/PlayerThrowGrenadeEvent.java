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

package com.tips48.rushMe.custom.events;

import com.tips48.rushMe.custom.items.Grenade;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PlayerThrowGrenadeEvent extends Event implements Cancellable {

	private static final long serialVersionUID = -753883085563151172L;
	private Player player;
	private Grenade grenade;
	private boolean cancelled;

	public PlayerThrowGrenadeEvent(Player player, Grenade grenade) {
		super("PlayerThrowGrenadeEvent");
		this.player = player;
		this.grenade = grenade;
		this.cancelled = false;
	}

	public Player getPlayer() {
		return player;
	}

	public Grenade getGrenade() {
		return grenade;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
