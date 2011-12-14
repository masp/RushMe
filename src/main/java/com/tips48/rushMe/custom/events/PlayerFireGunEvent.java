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

import com.tips48.rushMe.custom.items.Gun;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PlayerFireGunEvent extends Event implements Cancellable {

    private static final long serialVersionUID = 8735696261382213209L;
    private Player player;
    private Gun gun;
    private boolean cancel;

    public PlayerFireGunEvent(Player player, Gun gun) {
	super("PlayerFireGunEvent");
	this.player = player;
	this.gun = gun;
	this.cancel = false;
    }

    @Override
    public boolean isCancelled() {
	return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
	this.cancel = cancel;
    }

    public Player getPlayer() {
	return player;
    }

    public Gun getGun() {
	return gun;
    }

}
