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
import com.tips48.rushMe.packets.PacketInfo;
import com.tips48.rushMe.util.RMUtils;

import masp.tests.velocitytests.PlayerTestListener.CheckOnGround;
import net.minecraft.server.MathHelper;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Grenade extends GenericCustomItem implements Weapon {
	
	public static int placeTaskId = 0;
	public static CheckOnGround runInstance;
	
    private final GrenadeType type;
    private final Integer startAmount;
    private Integer amount;

    private final String shortName;

    private final Integer explosionSize;
    private final Integer timeBeforeExplosion;

    private final Integer damage;

    private final Integer stunTime;

    private final UUID uuid;

    protected Grenade(String name, String shortName, String texture,
	    GrenadeType type, Integer startAmount, Integer explosionSize,
	    Integer timeBeforeExplosion, Integer damage, Integer stunTime,
	    UUID uuid) {
	super(RushMe.getInstance(), name, texture);

	this.shortName = shortName;

	this.type = type;
	this.startAmount = startAmount;
	this.amount = startAmount;
	this.explosionSize = explosionSize;
	this.timeBeforeExplosion = timeBeforeExplosion;
	this.damage = damage;
	this.stunTime = stunTime;

	this.uuid = uuid;

	PacketGrenadeUpdate packet = new PacketGrenadeUpdate();
	packet.processGrenade(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
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

    public SpoutItemStack toItemStack(int amount) {
	return new SpoutItemStack(this, amount);
    }
    
    public class CheckOnGround implements Runnable {
		private ArrayList<Item> que = new ArrayList<Item>();
		private HashMap<UUID, Double> prevYs = new HashMap<UUID, Double>();
		private HashMap<UUID, Grenade> grenades = new HashMap<UUID, Grenade>();
		private HashMap<UUID, Player> players = new HashMap<UUID, Player>();
		RushMe plugin;
		private int taskId;
		
		public CheckOnGround(RushMe plugin, Item item, Grenade grenade, Player player) {
			this.plugin = plugin;
			que.add(item);
			prevYs.put(item.getUniqueId(), 0.0D);
			grenades.put(item.getUniqueId(), grenade);
			players.put(item.getUniqueId(), player);
		}
		
		public void addItem(Item item, Grenade grenade, Player player) {
			if (!que.contains(item)) {
				this.que.add(item);
				this.prevYs.put(item.getUniqueId(), 0.0D);
				grenades.put(item.getUniqueId(), grenade);
				players.put(item.getUniqueId(), player);
			}
		}
		
		public void setTaskId(int id) {
			this.taskId = id;
		}
		
		public void run() {
			if (!(que.size() == 0)) {
				for (Item item : que) {
					if (item.getFallDistance() <= 0) {
						Grenade grenade = grenades.get(item.getUniqueId());
						Player player = players.get(item.getUniqueId());
						grenade.onGroundHit(item, player);
						if (que.size() == 1) {
							plugin.getServer().getScheduler().cancelTask(this.taskId);
						} else {
							que.remove(que.indexOf(item));
							prevYs.remove(item.getUniqueId());
							grenades.remove(item.getUniqueId());
							players.remove(item.getUniqueId());
						}
					}
				}
			} else {
				plugin.getServer().getScheduler().cancelTask(taskId);
			}
		}
	}

    private void pathfind(Location loc, Player player) {
		Location result = null;
		Item eItem = player.getWorld().dropItem(player.getEyeLocation(), this.toItemStack(1));
		Vector nVector = new Vector();
		// 0.5 DEFAULT
		double speed = 1.0;
		double xHeading = -MathHelper.sin((float) ((player.getLocation().getYaw() * Math.PI) / 180F));
		double zHeading = MathHelper.cos((float) ((player.getLocation().getYaw() * Math.PI) / 180F));
		double motionX = speed * xHeading * MathHelper.cos((float) ((player.getLocation().getPitch() / 180F) * Math.PI));
		double motionY = -speed * MathHelper.sin((float) ((player.getLocation().getPitch() / 180F) * Math.PI));
		double motionZ = speed * zHeading*MathHelper.cos((float) ((player.getLocation().getPitch() / 180F) * Math.PI));
		nVector.setX(motionX);
		nVector.setY(motionY);
		nVector.setZ(motionZ);
		eItem.setVelocity(nVector);
		if (!RushMe.getInstance().getServer().getScheduler().isCurrentlyRunning(placeTaskId)) {
			CheckOnGround runnable = new CheckOnGround(RushMe.getInstance(), eItem, this, player);
			placeTaskId = RushMe.getInstance().getServer().getScheduler().scheduleAsyncRepeatingTask(RushMe.getInstance(), runnable, 0L, 5L);
			runnable.setTaskId(placeTaskId);
			runInstance = runnable;
		} else if (runInstance != null) {
			runInstance.addItem(eItem, this, player);
		}
    }
    
    public void onGroundHit(Item item, final Player player) {
		final Location droppedLocation = item.getLocation();
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
					    continue;
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
					if (!(e instanceof Player)) {
					    continue;
					}
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
							RushMe.getInstance(), s, 60, 3));
				    }
				    break;
				}

				PacketGrenadeUpdate packet = new PacketGrenadeUpdate();
				packet.processGrenade(g);
				packet.send(RMUtils.getSpoutPlayers());
				PacketInfo.addPacket(packet, packet);
			    }
			}, timeBeforeExplosion);
	}

    public void fire(final Player player) {
    	if (amount <= 0) {
    		return;
    	}
    	this.pathfind(player.getLocation(), player);
    }

    public String getShortName() {
	return shortName;
    }

    public UUID getUUID() {
	return uuid;
    }

    private class stun implements Runnable {
	private int taskId;
	private final SpoutPlayer sp;

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

    public boolean canBeUsed() {
	return amount > 0;
    }

}
