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

package com.tips48.rushMe.classes;

import com.tips48.rushMe.custom.items.Gun;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Class {
    private final String name;
    private final Set<Gun> allowedGuns;

    private final TIntSet players;

    protected Class(String name, Set<Gun> allowedGuns) {
	this.name = name;
	if (allowedGuns != null) {
	    this.allowedGuns = allowedGuns;
	} else {
	    this.allowedGuns = new HashSet<Gun>();
	}
	players = new TIntHashSet();
    }

    public String getName() {
	return name;
    }

    public Set<Gun> getAllowedGuns() {
	return allowedGuns;
    }

    public boolean isAllowed(Gun gun) {
	return allowedGuns.contains(gun);
    }

    public void addGun(Gun gun) {
	allowedGuns.add(gun);
    }

    public void removeGun(Gun gun) {
	if (allowedGuns.contains(gun)) {
	    allowedGuns.remove(gun);
	}
    }

    public TIntSet getPlayers() {
	return players;
    }

    public void addPlayer(Player player) {
	addPlayer(player.getEntityId());
    }

    public void addPlayer(int player) {
	players.add(player);
    }

    public void removePlayer(Player player) {
	removePlayer(player.getEntityId());
    }

    public void removePlayer(int player) {
	if (players.contains(player)) {
	    players.remove(player);
	}
    }

    public boolean containsPlayer(Player player) {
	return containsPlayer(player.getEntityId());
    }

    public boolean containsPlayer(int player) {
	return players.contains(player);
    }
}
