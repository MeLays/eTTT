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

public class AdvancedMaterial {
	
	Material m;
	byte data = 0;
	
	public AdvancedMaterial (Material m , byte data) {
		this.m = m;
		this.data = data;
	}
	
	public AdvancedMaterial (Material m) {
		this.m = m;
	}
	
	public Material getMaterial () {
		return m;
	}
	
	public byte getData() {
		return data;
	}
	
	public void updateBlock(Location loc) {
		updateBlock(loc.getBlock());
	}
	
	@SuppressWarnings("deprecation")
	public void updateBlock(Block b) {
		b.setType(m);
		b.setData(data);
	}
}
