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

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

public class RMEventListener extends CustomEventListener {

    @Override
    public void onCustomEvent(Event event) {
	if (event instanceof PlayerDamageEvent) {
	    onPlayerDamage((PlayerDamageEvent) event);
	} else if (event instanceof PlayerFireGunEvent) {
	    onPlayerFireGun((PlayerFireGunEvent) event);
	} else if (event instanceof PlayerThrowGrenadeEvent) {
	    onPlayerThrowGrenade((PlayerThrowGrenadeEvent) event);
	}
    }

    public void onPlayerDamage(PlayerDamageEvent event) {

    }

    public void onPlayerFireGun(PlayerFireGunEvent event) {

    }

    public void onPlayerThrowGrenade(PlayerThrowGrenadeEvent event) {

    }

}
