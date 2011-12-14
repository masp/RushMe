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

import com.tips48.rushMe.custom.items.Weapon;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PlayerDamageEvent extends Event implements Cancellable {

    private static final long serialVersionUID = 2697858965092148205L;
    private Player damager;
    private Player damaged;
    private int damage;
    private Weapon weapon;
    private boolean cancel;

    public PlayerDamageEvent(Player damaged, Player damager, int damage,
	    Weapon weapon) {
	super("PlayerDamageEvent");
	this.damaged = damaged;
	this.damager = damager;
	this.damage = damage;
	this.weapon = weapon;
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

    public Player getDamager() {
	return damager;
    }

    public Player getDamaged() {
	return damaged;
    }

    public int getDamage() {
	return damage;
    }

    public void setDamage(int damage) {
	this.damage = damage;
    }

    public Weapon getWeapon() {
	return weapon;
    }

}
