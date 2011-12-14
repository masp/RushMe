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

package com.tips48.rushMe.gamemodes;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public enum GameModeType {

    OBJECTIVE(0), DEATHMATCH(1), CAPTURE(2), FLAG(3);

    private final int code;
    private static final TIntObjectMap<GameModeType> types;

    private GameModeType(int code) {
	this.code = code;
    }

    public int getCode() {
	return this.code;
    }

    public static GameModeType getByCode(int code) {
	return types.get(code);
    }

    static {
	types = new TIntObjectHashMap<GameModeType>();

	for (GameModeType gt : values())
	    types.put(gt.getCode(), gt);
    }

}
