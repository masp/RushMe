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
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ClassManager {

	private static final Set<Class> classes = new HashSet<Class>();

	public static Class createClass(String name, Set<Gun> allowedGuns) {
		Class c = new Class(name, allowedGuns);

		classes.add(c);

		return c;
	}

	public static Class getClass(String name) {
		for (Class c : classes) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public static Class getClassOf(Player player) {
		return getClassOf(player.getEntityId());
	}

	public static Class getClassOf(int player) {
		for (Class c : classes) {
			if (c.containsPlayer(player)) {
				return c;
			}
		}
		return null;
	}

}
