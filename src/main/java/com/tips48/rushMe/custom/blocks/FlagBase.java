package com.tips48.rushMe.custom.blocks;

import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.tips48.rushMe.RushMe;

public class FlagBase extends GenericCustomBlock {
	public FlagBase() {
		super(RushMe.getInstance(), "FlagBase", true);

		// TODO block model and id
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World arg0, int x, int y, int z,
			BlockFace bf) {
		return true;
	}

	@Override
	public boolean isIndirectlyProdivingPowerTo(World world, int x, int y,
			int z, BlockFace bf) {
		return true;
	}

	@Override
	public boolean isProvidingPowerTo(World world, int x, int y, int z,
			BlockFace bf) {
		return true;
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z,
			SpoutPlayer sp) {
	}

	@Override
	public void onBlockDestroyed(World world, int x, int y, int z) {
	}

	@Override
	public boolean onBlockInteract(World world, int x, int y, int z,
			SpoutPlayer sp) {
		return true;
	}

	@Override
	public void onBlockPlace(World world, int x, int y, int z) {
	}

	@Override
	public void onBlockPlace(World world, int x, int y, int z,
			LivingEntity placer) {
	}

	@Override
	public void onEntityMoveAt(World world, int x, int y, int z,
			Entity mover) {
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z,
			int unknown) {
		// TODO find out 4th arg
	}
}
