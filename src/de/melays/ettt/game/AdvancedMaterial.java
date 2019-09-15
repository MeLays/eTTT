/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class AdvancedMaterial {
	
	Material m;
	BlockData data = null;
	
	public AdvancedMaterial (Material m , BlockData data) {
		this.m = m;
		this.data = data;
	}
	
	public AdvancedMaterial (Material m) {
		this.m = m;
	}
	
	public Material getMaterial () {
		return m;
	}
	
	public BlockData getData() {
		return data;
	}
	
	public void updateBlock(Location loc) {
		updateBlock(loc.getBlock());
	}
	
	public void updateBlock(Block b) {
		b.setType(m);
		if (data != null)
			b.setBlockData(data);
	}
}
