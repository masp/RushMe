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
import com.tips48.rushMe.custom.GUI.SpoutGUI;
import com.tips48.rushMe.packets.PacketGunUpdate;
import com.tips48.rushMe.packets.PacketInfo;
import com.tips48.rushMe.util.RMUtils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomItem;

import java.util.UUID;

public class Gun extends GenericCustomItem {
    // LAST FIRED
    private long lastFired;
    // RELOAD
    private boolean reloading;
    private final int reloadTime;
    // AMMO
    private final int maxClipSize;
    private int loadedInClip;
    private int ammo;
    private final int maxAmmo;
    // OTHER
    private Double timeBetweenFire;
    private boolean autoReload;
    private final Double recoilBack;
    private final Float recoilVertical;
    private final Float recoilHorizontal;
    // EXPLOSIONS
    private final boolean bulletsExplode;
    private final Float explosionSize;
    private final Double entityDamageDistance;
    // DAMAGE
    private final Integer headshotDamage;
    private final Integer bodyDamage;

    private final UUID uuid;

    protected Gun(String name, String texture, Integer reloadTime,
	    Boolean autoReload, Integer maxClipSize, Integer maxAmmo,
	    Double timeBetweenFire, Boolean bulletsExplode,
	    Float explosionSize, Double entityDamageDistance,
	    Integer headshotDamage, Integer bodyDamage, Double recoilBack,
	    Float recoilVertical, Float recoilHorizontal, UUID uuid) {
	super(RushMe.getInstance(), name, texture);

	this.reloadTime = reloadTime;
	this.maxClipSize = maxClipSize;
	this.loadedInClip = maxClipSize;
	this.autoReload = autoReload;
	this.maxAmmo = maxAmmo;
	this.ammo = maxAmmo;
	this.timeBetweenFire = timeBetweenFire;
	this.bulletsExplode = bulletsExplode;
	this.explosionSize = explosionSize;
	this.entityDamageDistance = entityDamageDistance;
	this.headshotDamage = headshotDamage;
	this.bodyDamage = bodyDamage;
	this.recoilBack = recoilBack;
	this.recoilVertical = recoilVertical;
	this.recoilHorizontal = recoilHorizontal;

	this.uuid = uuid;

	PacketGunUpdate packet = new PacketGunUpdate();
	packet.processGun(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public boolean getBulletsExplode() {
	return bulletsExplode;
    }

    public Float getExplosionSize() {
	return explosionSize;
    }

    public int getHeadshotDamage() {
	return headshotDamage;
    }

    public int getBodyDamage() {
	return bodyDamage;
    }

    public int getAmmo() {
	return ammo;
    }

    public void setAmmo(int ammo) {
	if (maxAmmo - ammo >= 0) {
	    this.ammo = ammo;
	    return;
	}
	this.ammo = maxAmmo;
	PacketGunUpdate packet = new PacketGunUpdate();
	packet.processGun(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public int getMaxAmmo() {
	return maxAmmo;
    }

    public int getLoadedInClip() {
	return loadedInClip;
    }

    public void setLoadedInClip(int loadedInClip) {
	if (maxClipSize - loadedInClip >= 0) {
	    this.loadedInClip = loadedInClip;
	    return;
	}
	this.loadedInClip = maxClipSize;
	PacketGunUpdate packet = new PacketGunUpdate();
	packet.processGun(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public int getMaxClipSize() {
	return maxClipSize;
    }

    public double getTimeBetweenFire() {
	return timeBetweenFire;
    }

    public void setTimeBetweenFire(double timeBetweenFire) {
	this.timeBetweenFire = timeBetweenFire;
	PacketGunUpdate packet = new PacketGunUpdate();
	packet.processGun(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public int getReloadTime() {
	return reloadTime;
    }

    public SpoutItemStack toItemStack(int amount) {
	return new SpoutItemStack(this, amount);
    }

    public boolean canFire() {
	return !(reloading || loadedInClip <= 0 || System.currentTimeMillis()
		- lastFired < timeBetweenFire * 100);
    }

    public void fire(Player player) {
	--loadedInClip;
	SpoutGUI.getHudOf(player).updateHUD();
	lastFired = System.currentTimeMillis();
	if (!(player.isSneaking())) {
	    player.setVelocity(player.getLocation().getDirection()
		    .multiply(-recoilBack));
	    Location loc = player.getLocation();
	    loc.setPitch(loc.getPitch() + -recoilVertical);
	    loc.setYaw(loc.getYaw() + recoilHorizontal);
	    player.teleport(loc);
	} else {
	    player.setVelocity(player.getLocation().getDirection()
		    .multiply(-recoilBack / 2));
	    Location loc = player.getLocation();
	    loc.setPitch(loc.getPitch() + -recoilVertical / 2);
	    loc.setYaw(loc.getYaw() + recoilHorizontal / 2);
	    player.teleport(loc);
	}
	if (loadedInClip <= 0) {
	    if (autoReload) {
		reload(player);
	    }
	}
	PacketGunUpdate packet = new PacketGunUpdate();
	packet.processGun(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public void reload(final Player player) {
	if (ammo <= 0) {
	    return;
	}
	final Gun gun = this;
	reloading = true;
	RushMe.getInstance().getServer().getScheduler()
		.scheduleSyncDelayedTask(RushMe.getInstance(), new Runnable() {
		    public void run() {
			if (!(reloading)) {
			    return;
			}
			if (ammo - maxClipSize >= 0) {
			    int lastLoadedInClip = loadedInClip;
			    loadedInClip = maxClipSize;
			    ammo = ammo - (maxClipSize - lastLoadedInClip);
			} else {
			    loadedInClip = ammo;
			    ammo = 0;
			}
			reloading = false;
			SpoutGUI.getHudOf(player).updateHUD();
			PacketGunUpdate packet = new PacketGunUpdate();
			packet.processGun(gun);
			packet.send(RMUtils.getSpoutPlayers());
			PacketInfo.addPacket(packet, packet);
		    }
		}, reloadTime);
    }

    public boolean isAutoReload() {
	return autoReload;
    }

    public void setAutoReload(boolean autoReload) {
	this.autoReload = autoReload;
	PacketGunUpdate packet = new PacketGunUpdate();
	packet.processGun(this);
	packet.send(RMUtils.getSpoutPlayers());
	PacketInfo.addPacket(packet, packet);
    }

    public double getEntityDamageDistance() {
	return entityDamageDistance;
    }

    public UUID getUUID() {
	return uuid;
    }

    @Override
    public String toString() {
	return this.getName();
    }

}
