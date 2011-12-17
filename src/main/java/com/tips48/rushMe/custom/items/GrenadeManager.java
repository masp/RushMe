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

import com.tips48.rushMe.RushMe;

import org.bukkit.entity.Player;
import org.getspout.spoutapi.material.CustomItem;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.*;
import java.util.logging.Level;

public class GrenadeManager {

    private static final Set<Grenade> grenades = new HashSet<Grenade>();

    private static final TIntObjectMap<List<Grenade>> playerGrenades = new TIntObjectHashMap<List<Grenade>>();
    private static final TIntIntMap selectedIndex = new TIntIntHashMap();

    private GrenadeManager() {

    }

    public static Grenade createGrenade(String name, String shortName,
	    String texture, GrenadeType type, Integer startAmount,
	    Integer explosionSize, Integer timeBeforeExplosion, Integer damage,
	    Integer stunTime, UUID uuid) {

	Grenade grenade = new Grenade(name, shortName, texture, type,
		startAmount, explosionSize, timeBeforeExplosion, damage,
		stunTime, uuid == null ? UUID.randomUUID() : uuid);

	grenades.add(grenade);

	RushMe.getInstance()
		.getLogger()
		.debugLog(Level.INFO,
			"Created grenade " + name + ".  Atributes:");
	RushMe.getInstance()
		.getLogger()
		.debugLog(
			Level.INFO,
			"ShortName = " + shortName + ";Type = " + type
				+ ";StartAmount = " + startAmount
				+ ";ExplosionSize = " + explosionSize
				+ ";TimeBeforeExplosion = "
				+ timeBeforeExplosion + ";Damage = " + damage
				+ ";StunTime = " + stunTime);

	return grenade;
    }

    public static Set<Grenade> getGrenades() {
	return grenades;
    }

    public static Grenade getGrenade(String name) {
	for (Grenade g : getGrenades()) {
	    if (g.getName().equals(name)) {
		return g;
	    }
	}
	return null;
    }

    public static Grenade getGrenade(CustomItem item) {
	for (Grenade g : getGrenades()) {
	    if (item.equals(g)) {
		return g;
	    }
	}
	return null;
    }

    public static Grenade getGrenade(UUID uuid) {
	for (Grenade g : getGrenades()) {
	    if (g.getUUID().equals(uuid)) {
		return g;
	    }
	}
	return null;
    }

    public static Grenade getSelectedGrenade(Player player) {
	return getSelectedGrenade(player.getEntityId());
    }

    public static Grenade getSelectedGrenade(int player) {
	if (!(playerGrenades.containsKey(player))) {
	    return null;
	}
	return playerGrenades.get(player).get(selectedIndex.get(player));
    }

    public static Set<String> getGrenadeNames() {
	Set<String> names = new HashSet<String>();
	for (Grenade g : getGrenades()) {
	    names.add(g.getName());
	}
	return names;
    }

    public static List<Grenade> getGrenades(Player player) {
	return getGrenades(player.getEntityId());
    }

    public static List<Grenade> getGrenades(int player) {
	return playerGrenades.get(player);
    }

    public static void createGrenades(int player) {
	playerGrenades.put(player, toList());
    }

    public static void createGrenades(Player player) {
	createGrenades(player.getEntityId());
    }

    private static List<Grenade> toList() {
	List<Grenade> result = new ArrayList<Grenade>(grenades.size());
	for (Grenade g : grenades) {
	    result.add(g);
	}
	return result;
    }

}