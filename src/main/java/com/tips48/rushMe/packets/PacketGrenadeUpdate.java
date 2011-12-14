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

package com.tips48.rushMe.packets;

import com.tips48.rushMe.custom.items.*;
import com.tips48.rushMe.util.RMLogging;

import org.getspout.spoutapi.io.*;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.UUID;
import java.util.logging.Level;

public class PacketGrenadeUpdate extends AddonPacket implements PriorityPacket {

	private String name;
	private String shortName;
	private int typeInt;
	private GrenadeType type;
	private int amount;
	private int startAmount;
	private int explosionSize;
	private int timeBeforeExplosion;
	private int damage;
	private int stunTime;
	private UUID uuid;

	@Override
	public void read(SpoutInputStream stream) {
		name = stream.readString();
		shortName = stream.readString();
		typeInt = stream.readInt();
		try {
			type = GrenadeType.getByCode(typeInt);
		} catch (Exception e) {
			RMLogging.log(e, "PacketGrenadeUpdate was sent wrongly!");
		}
		amount = stream.readInt();
		startAmount = stream.readInt();
		explosionSize = stream.readInt();
		timeBeforeExplosion = stream.readInt();
		damage = stream.readInt();
		stunTime = stream.readInt();
		uuid = UUID.fromString(stream.readString());
		RMLogging.debugLog(Level.INFO, "Read PacketGrenadeUpdate.  Atributes:");
		RMLogging.debugLog(Level.INFO, "Name = " + name + ";ShortName = "
				+ shortName + ";TypeInt = " + typeInt + ";Type = " + type
				+ ";Amount = " + amount + ";StartAmount = " + startAmount
				+ ";ExplosionSize = " + explosionSize
				+ ";TimeBeforeExplosion = " + timeBeforeExplosion
				+ ";Damage = " + damage + ";StunTime = " + stunTime
				+ ";UUID = " + uuid);
	}

	@Override
	public void run(SpoutPlayer sp) {
		RMLogging.debugLog(Level.INFO,
				"Running PacketGrenadeUpdate for " + sp.getName());
		Grenade grenade = GrenadeManager.getGrenade(name);
		if (grenade == null) {
			grenade = GrenadeManager.createGrenade(name, null, shortName, type,
					startAmount, explosionSize, timeBeforeExplosion, damage,
					stunTime, uuid);
		}
		grenade.setAmount(amount);
	}

	@Override
	public void write(SpoutOutputStream stream) {
		stream.writeString(name);
		stream.writeString(shortName);
		stream.writeInt(typeInt);
		stream.writeInt(amount);
		stream.writeInt(startAmount);
		stream.writeInt(explosionSize);
		stream.writeInt(timeBeforeExplosion);
		stream.writeInt(damage);
		stream.writeInt(stunTime);
		stream.writeString(uuid.toString());
		RMLogging.debugLog(Level.INFO, "Read PacketGrenadeUpdate.  Atributes:");
		RMLogging.debugLog(Level.INFO, "Name = " + name + ";ShortName = "
				+ shortName + ";TypeInt = " + typeInt + ";Type = " + type
				+ ";Amount = " + amount + ";StartAmount = " + startAmount
				+ ";ExplosionSize = " + explosionSize
				+ ";TimeBeforeExplosion = " + timeBeforeExplosion
				+ ";Damage = " + damage + ";StunTime = " + stunTime
				+ ";UUID = " + uuid);
	}

	public GrenadeType getType() {
		return type;
	}

	public void setType(GrenadeType type) {
		this.type = type;
		typeInt = type.getCode();
	}

	public Integer getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(Integer startAmount) {
		this.startAmount = startAmount;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Integer getExplosionSize() {
		return explosionSize;
	}

	public void setExplosionSize(Integer explosionSize) {
		this.explosionSize = explosionSize;
	}

	public Integer getTimeBeforeExplosion() {
		return timeBeforeExplosion;
	}

	public void setTimeBeforeExplosion(Integer timeBeforeExplosion) {
		this.timeBeforeExplosion = timeBeforeExplosion;
	}

	public Integer getDamage() {
		return damage;
	}

	public void setDamage(Integer damage) {
		this.damage = damage;
	}

	public Integer getStunTime() {
		return stunTime;
	}

	public void setStunTime(Integer stunTime) {
		this.stunTime = stunTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	public PacketGrenadeUpdate processGrenade(Grenade grenade) {
		setName(grenade.getName());
		setShortName(grenade.getShortName());
		setType(grenade.getType());
		setAmount(grenade.getAmount());
		setStartAmount(grenade.getStartAmount());
		setExplosionSize(grenade.getExplosionSize());
		setTimeBeforeExplosion(grenade.getTimeBeforeExplosion());
		setDamage(grenade.getDamage());
		setStunTime(grenade.getStunTime());
		setUUID(grenade.getUUID());
		return this;
	}

	@Override
	public PacketPriority getPriority() {
		return PacketPriority.LOWEST;
	}

}
