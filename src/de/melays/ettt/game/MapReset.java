package de.melays.ettt.game;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class MapReset {
	
	public MapReset () {
		
	}
	
	//Placed Blocks
	
	HashMap<Location , AdvancedMaterial> placed = new HashMap<Location , AdvancedMaterial>();
	
	public void addPlacedBlock(Location loc , AdvancedMaterial from) {
		if (from == null) from = new AdvancedMaterial(Material.AIR);
		placed.put(loc, from);
	}
	
	public void resetPlacedBlocks() {
		for (Location loc : this.placed.keySet()) {
			this.placed.get(loc).updateBlock(loc);
		}
		this.placed.clear();
	}
	
	//Removed Blocks
	
	HashMap<Location , AdvancedMaterial> removed = new HashMap<Location , AdvancedMaterial>();
	
	public void addRemovedBlock(Location loc , AdvancedMaterial removed) {
		this.removed.put(loc, removed);
	}
	
	public void resetRemovedBlocks() {
		for (Location loc : this.removed.keySet()) {
			this.removed.get(loc).updateBlock(loc);
		}
		this.removed.clear();
	}
	
	//Items & Arrows (Entities)
	
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public void addEntity (Entity e) {
		entities.add(e);
	}
	
	public void resetEntities () {
		for (Entity e : entities) {
			if (e == null) continue;
			if (e.isDead()) continue;
			e.remove();
		}
		entities.clear();
	}
	
	//Reset all
	public void resetAll() {
		this.resetEntities();
		this.resetPlacedBlocks();
		this.resetRemovedBlocks();
	}
	
	
}
