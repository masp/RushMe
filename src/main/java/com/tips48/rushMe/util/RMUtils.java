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

package com.tips48.rushMe.util;

import com.tips48.rushMe.RushMe;
import com.tips48.rushMe.custom.items.*;

import net.minecraft.server.MathHelper;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class RMUtils {

    private RMUtils() {

    }

    public static String readableArray(Object[] objects) {
	StringBuilder sb = new StringBuilder();
	for (Object o : objects) {
	    sb.append(", ").append(o.toString());
	}
	return sb.toString().replaceFirst(", ", "").replace("[", "")
		.replace("]", "");
    }

    public static String readableList(List<?> objects) {
	StringBuilder sb = new StringBuilder();
	for (Object o : objects) {
	    sb.append(", ").append(o.toString());
	}
	return sb.toString().replaceFirst(", ", "").replace("[", "")
		.replace("]", "");
    }

    public static String readableSet(Set<?> objects) {
	StringBuilder sb = new StringBuilder();
	for (Object o : objects) {
	    sb.append(", ").append(o.toString());
	}
	return sb.toString().replaceFirst(", ", "").replace("[", "")
		.replace("]", "");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Set<?> toSet(TIntSet ts) {
	Set result = new HashSet();
	for (Integer i : ts.toArray()) {
	    result.add(i);
	}
	return result;
    }

    public static String parseIntForMinute(Integer i) {
	int i2 = i;
	int minutes = 0;
	boolean stop = false;
	while (!stop) {
	    if ((i2 - 60) >= 0) {
		minutes++;
		i2 -= 60;
	    } else {
		stop = true;
	    }
	}
	String minuteResult = Integer.toString(minutes);
	String i2Result = Integer.toString(i2);
	if (minutes == 0) {
	    minuteResult = "00";
	} else if (!((minutes - 10) >= 0)) {
	    minuteResult = "0" + Integer.toString(minutes);
	}
	if (i2 == 0) {
	    i2Result = "00";
	} else if (!((i2 - 10) >= 0)) {
	    i2Result = "0" + Integer.toString(i2);
	}
	return minuteResult + ":" + i2Result;
    }

    public static boolean isHoldingGun(Player player) {
	return isGun(player.getItemInHand());
    }

    public static void giveAllGuns(Player player) {
	for (Gun g : GunManager.getGuns()) {
	    player.getInventory().addItem(g.toItemStack(1));
	}
    }

    public static boolean isGun(ItemStack item) {
	if (item == null) {
	    return false;
	}
	SpoutItemStack sis = new SpoutItemStack(item);
	if (sis.isCustomItem()) {
	    CustomItem i = (CustomItem) sis.getMaterial();
	    Gun g = GunManager.getGun(i);
	    if (g != null) {
		return true;
	    }
	}
	return false;
    }

    public static void clearInventoryOfGuns(Player player) {
	if (player == null) {
	    return;
	}
	ItemStack[] inventory = player.getInventory().getContents();
	ItemStack[] armor = player.getInventory().getArmorContents();
	player.getInventory().clear();
	for (ItemStack item : inventory) {
	    if (item == null) {
		continue;
	    }
	    if (!isGun(item)) {
		player.getInventory().addItem(item);
	    }
	}
	player.getInventory().setArmorContents(armor);
    }

    public static Gun getGunOf(Player player) {
	if (!(isHoldingGun(player))) {
	    return null;
	}
	CustomItem i = (CustomItem) new SpoutItemStack(player.getItemInHand())
		.getMaterial();
	return GunManager.getGun(i);
    }

    public static List<Entity> getNearbyEntities(Location loc, double radiusX,
	    double radiusY, double radiusZ) {
	Entity e = loc.getWorld().spawn(loc, Minecart.class);
	List<Entity> entities = e.getNearbyEntities(radiusX, radiusY, radiusZ);
	e.remove();
	return entities;
    }

    public static void createAstheticExplosion(Grenade g, Location loc) {

	Set<Block> blocks = new HashSet<Block>();

	float f = g.getExplosionSize();
	byte b0 = 16;

	int i;
	int j;
	int k;

	double d0;
	double d1;
	double d2;

	for (i = 0; i < b0; ++i) {
	    for (j = 0; j < b0; ++j) {
		for (k = 0; k < b0; ++k) {
		    if ((i == 0) || (i == (b0 - 1)) || (j == 0)
			    || (j == (b0 - 1)) || (k == 0) || (k == (b0 - 1))) {
			double d3 = (((i / (b0 - 1.0F)) * 2.0F) - 1.0F);
			double d4 = (((j / (b0 - 1.0F)) * 2.0F) - 1.0F);
			double d5 = (((k / (b0 - 1.0F)) * 2.0F) - 1.0F);
			double d6 = Math
				.sqrt((d3 * d3) + (d4 * d4) + (d5 * d5));

			d3 /= d6;
			d4 /= d6;
			d5 /= d6;
			float f1 = f
				* (0.7F + (new Random().nextFloat() * 0.6F));

			d0 = loc.getX();
			d1 = loc.getY();
			d2 = loc.getZ();

			for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) {
			    int l = MathHelper.floor(d0);
			    int i1 = MathHelper.floor(d1);
			    int j1 = MathHelper.floor(d2);
			    int k1 = loc.getWorld().getBlockAt(l, i1, j1)
				    .getTypeId();

			    if (k1 > 0) {
				f1 -= (net.minecraft.server.Block.byId[k1]
					.a((net.minecraft.server.Entity) null) + 0.3F)
					* f2;
			    }

			    if (f1 > 0.0F) {
				Block b = loc.getWorld().getBlockAt(l, i1, j1);
				if (b.getType() != Material.AIR) {
				    blocks.add(b);
				}
			    }

			    d0 += d3 * f2;
			    d1 += d4 * f2;
			    d2 += d5 * f2;
			}
		    }
		}
	    }
	}
	for (Block b : blocks) {
	    b.setTypeId(0);
	}
    }

    public static void createAstheticExplosion(Gun g, Location loc) {

	Set<Block> blocks = new HashSet<Block>();

	float f = g.getExplosionSize();
	byte b0 = 16;

	int i;
	int j;
	int k;

	double d0;
	double d1;
	double d2;

	for (i = 0; i < b0; ++i) {
	    for (j = 0; j < b0; ++j) {
		for (k = 0; k < b0; ++k) {
		    if ((i == 0) || (i == (b0 - 1)) || (j == 0)
			    || (j == (b0 - 1)) || (k == 0) || (k == (b0 - 1))) {
			double d3 = (((i / (b0 - 1.0F)) * 2.0F) - 1.0F);
			double d4 = (((j / (b0 - 1.0F)) * 2.0F) - 1.0F);
			double d5 = (((k / (b0 - 1.0F)) * 2.0F) - 1.0F);
			double d6 = Math
				.sqrt((d3 * d3) + (d4 * d4) + (d5 * d5));

			d3 /= d6;
			d4 /= d6;
			d5 /= d6;
			float f1 = f
				* (0.7F + (new Random().nextFloat() * 0.6F));

			d0 = loc.getX();
			d1 = loc.getY();
			d2 = loc.getZ();

			for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) {
			    int l = MathHelper.floor(d0);
			    int i1 = MathHelper.floor(d1);
			    int j1 = MathHelper.floor(d2);
			    int k1 = loc.getWorld().getBlockAt(l, i1, j1)
				    .getTypeId();

			    if (k1 > 0) {
				f1 -= (net.minecraft.server.Block.byId[k1]
					.a((net.minecraft.server.Entity) null) + 0.3F)
					* f2;
			    }

			    if (f1 > 0.0F) {
				Block b = loc.getWorld().getBlockAt(l, i1, j1);
				if (b.getType() != Material.AIR) {
				    blocks.add(b);
				}
			    }

			    d0 += d3 * f2;
			    d1 += d4 * f2;
			    d2 += d5 * f2;
			}
		    }
		}
	    }
	}
	for (Block b : blocks) {
	    b.setTypeId(0);
	}
    }

    /**
     * This code was contributed to RushMe by AJCStriker, from the
     * CounterCraftDev team. Original file can be found at
     * https://github.com/AJCStriker/Counter-
     * Craft/edit/master/src/net/countercraft/ccserver/maths/MathsHelper.java
     */
    public static TIntSet spot(Player spotter) {
	TIntSet spottedList = new TIntHashSet();
	for (Player player : RushMe.getInstance().getServer()
		.getOnlinePlayers()) {
	    if (!player.equals(spotter)) {
		double x = spotter.getLocation().toVector()
			.distance(player.getLocation().toVector());

		Vector direction = spotter.getLocation().getDirection()
			.multiply(x);

		Vector answer = direction.add(spotter.getLocation().toVector());

		if (((CraftLivingEntity) player).getHandle().g(
			((CraftEntity) spotter).getHandle())) {
		    if (answer.distance(player.getLocation().toVector()) < 1.37) {
			spottedList.add(player.getEntityId());
		    }
		}
	    }
	}
	return spottedList;
    }

    public static List<SpoutPlayer> getSpoutPlayers() {
	Player[] players = RushMe.getInstance().getServer().getOnlinePlayers();
	List<SpoutPlayer> result = new ArrayList<SpoutPlayer>(players.length);
	for (Player player : players) {
	    result.add(SpoutManager.getPlayer(player));
	}
	return result;
    }
}