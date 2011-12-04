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

import java.util.HashSet;
import java.util.Set;

public class Class {
	private String name;
	private Set<Gun> allowedGuns;

	public Class(String name, Set<Gun> allowedGuns) {
		this.name = name;
		if (allowedGuns != null) {
			this.allowedGuns = allowedGuns;
		} else {
			this.allowedGuns = new HashSet<Gun>();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
}
