package com.tips48.rushMe.custom.items;

import java.util.UUID;

import org.getspout.spoutapi.inventory.SpoutItemStack;

public interface Weapon {
    
    public String getName();
    
    public SpoutItemStack toItemStack(int amount);
    
    public UUID getUUID();

}
