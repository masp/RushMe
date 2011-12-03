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
import com.tips48.rushMe.custom.GUI.MainHUD;
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.data.PlayerData;
import com.tips48.rushMe.packets.PacketGrenadeUpdate;
import com.tips48.rushMe.util.RMUtils;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Grenade extends GenericCustomItem {

	private GrenadeType type;
	private Integer startAmount;
	private Integer amount;

	private final String shortName;

	private Integer explosionSize;
	private Integer timeBeforeExplosion;

	private Integer damage;

	private Integer stunTime;

	protected Grenade(String name, String shortName, String texture,
			GrenadeType type, Integer startAmount, Integer explosionSize,
			Integer timeBeforeExplosion, Integer damage, Integer stunTime) {
		super(RushMe.getInstance(), name, texture);

		this.shortName = shortName;

		this.type = type;
		this.startAmount = startAmount;
		this.amount = startAmount;
		this.explosionSize = explosionSize;
		this.timeBeforeExplosion = timeBeforeExplosion;
		this.damage = damage;
		this.stunTime = stunTime;

		PacketGrenadeUpdate packet = new PacketGrenadeUpdate();
		packet.processGrenade(this);
		packet.send(RMUtils.getSpoutPlayers());
	}

	public GrenadeType getType() {
		return type;
	}

	public Integer getStartAmount() {
		return startAmount;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
		PacketGrenadeUpdate packet = new PacketGrenadeUpdate();
		packet.processGrenade(this);
		packet.send(RMUtils.getSpoutPlayers());
	}

	public Integer getExplosionSize() {
		return explosionSize;
	}

	public Integer getTimeBeforeExplosion() {
		return timeBeforeExplosion;
	}

	public Integer getDamage() {
		return damage;
	}

	public Integer getStunTime() {
		return stunTime;
	}

	public void fire(final Player player, Location loc) {
		if (amount <= 0) {
			return;
		}
		final Location droppedLocation = null;
		final Grenade g = this;
		RushMe.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(RushMe.getInstance(), new Runnable() {
					public void run() {
						switch (type) {
						case FRAG:
							RMUtils.createAstheticExplosion(g, droppedLocation);
							for (Entity e : RMUtils.getNearbyEntities(
									droppedLocation, explosionSize,
									explosionSize, explosionSize)) {
								if (e instanceof Player) {
									PlayerData.registerDamage((Player) e,
											player, damage, g);
								} else {
									if (e instanceof LivingEntity) {
										((LivingEntity) e).damage(damage);
									}
								}
							}
							break;
						case CONCUSSION:
							for (Entity e : RMUtils.getNearbyEntities(
									droppedLocation, explosionSize,
									explosionSize, explosionSize)) {
								if (!(e instanceof Player)) {
									return;
								}
								Player p = (Player) e;
								MainHUD hud = SpoutGUI.getHudOf(p);
								if (hud != null) {
									int distance = (int) p.getLocation()
											.distance(droppedLocation);
									hud.doConcussion(255 / distance, stunTime);
								}
							}
							break;
						case STUN:
							for (Entity e : RMUtils.getNearbyEntities(
									droppedLocation, explosionSize,
									explosionSize, explosionSize)) {
								if (e instanceof Player) {
									final SpoutPlayer sp = SpoutManager
											.getPlayer((Player) e);
									sp.setJumpingMultiplier(.3);
									sp.setWalkingMultiplier(.3);
									sp.setSwimmingMultiplier(.3);
									stun s = new stun(sp);
									s.setTaskId(RushMe
											.getInstance()
											.getServer()
											.getScheduler()
											.scheduleSyncRepeatingTask(
													RushMe.getInstance(), s,
													60, 3));
								}
							}
							break;
						}

						PacketGrenadeUpdate packet = new PacketGrenadeUpdate();
						packet.processGrenade(g);
						packet.send(RMUtils.getSpoutPlayers());
					}
				}, timeBeforeExplosion);
	}

	public String getShortName() {
		return shortName;
	}

	private class stun implements Runnable {
		private int taskId;
		private SpoutPlayer sp;

		public stun(SpoutPlayer sp) {
			this.sp = sp;
		}

		public void run() {
			double now = sp.getWalkingMultiplier();
			sp.setWalkingMultiplier(now + .1);
			sp.setJumpingMultiplier(now + .1);
			sp.setSwimmingMultiplier(now + 0.1);
			if (sp.getWalkingMultiplier() == 1) {
				RushMe.getInstance().getServer().getScheduler()
						.cancelTask(taskId);
			}
		}

		public void setTaskId(int id) {
			this.taskId = id;
		}
	}

}
