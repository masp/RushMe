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

package com.tips48.rushMe.custom.items;

import com.tips48.rushMe.util.RMLogging;
import org.getspout.spoutapi.material.CustomItem;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class GunManager {
	private static final Set<Gun> guns = new HashSet<Gun>();

	private GunManager() {

	}

	public static Gun createGun(String name, String texture,
			Integer reloadTime, Boolean autoReload, Integer maxClipSize,
			Integer maxAmmo, Double timeBetweenFire, Boolean bulletsExplode,
			Float explosionSize, Double entityDamageDistance,
			Integer headshotDamage, Integer bodyDamage, Double recoilBack,
			Float recoilVertical, Float recoilHorizontal) {

		Gun gun = new Gun(name, texture, reloadTime, autoReload, maxClipSize,
				maxAmmo, timeBetweenFire, bulletsExplode, explosionSize,
				entityDamageDistance, headshotDamage, bodyDamage, recoilBack,
				recoilVertical, recoilHorizontal);

		guns.add(gun);

		RMLogging.debugLog(Level.INFO, "Created gun " + gun + ".  Atributes:");
		RMLogging.debugLog(Level.INFO, "ReloadTime = " + reloadTime + ";MaxClipSize = " + maxClipSize + ";MaxAmmo = " + maxAmmo + ";TimeBetweenFire = " + timeBetweenFire + ";AutoReload = " + autoReload + ";BulletsExplode = " + bulletsExplode + ";ExplosionSize = " + explosionSize + ";EntityDamageDistance = " + entityDamageDistance + ";HeadshotDamage = " + headshotDamage + ";BodyDamage = " + bodyDamage);

		return gun;
	}

	public static Set<Gun> getGuns() {
		return guns;
	}

	public static Gun getGun(String name) {
		for (Gun g : getGuns()) {
			if (g.getName().equals(name)) {
				return g;
			}
		}
		return null;
	}

	public static Gun getGun(CustomItem item) {
		for (Gun g : getGuns()) {
			if (item.equals(g)) {
				return g;
			}
		}
		return null;
	}

	public static Set<String> getGunNames() {
		Set<String> names = new HashSet<String>();
		for (Gun g : getGuns()) {
			names.add(g.getName());
		}
		return names;
	}
}
