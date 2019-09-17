package de.melays.ettt.game;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class CorpseContainer {
	
	RoleManager roleManager;
	
	HashMap<UUID , CorpseData> corpses = new HashMap<UUID , CorpseData>();
	HashMap<UUID , Boolean> found = new HashMap<UUID , Boolean>();
	
	public CorpseContainer(RoleManager roleManager) {
		this.roleManager = roleManager;
	}
	
	public void connectCorpse(CorpseData data , Player p) {
		corpses.put(p.getUniqueId(), data);
	}
	
	public CorpseData getCorpse (Player p) {
		if (corpses.containsKey(p.getUniqueId())) {
			return corpses.get(p.getUniqueId());
		}
		return null;
	}
	
	public UUID getPlayer (int corpseId) {
		for (UUID uuid : corpses.keySet()){
			CorpseData corpse = corpses.get(uuid);
			if (corpse.getEntityId() == corpseId) {
				return uuid;
			}
		}
		return null;
	}
	
	public void removeAll() {
		for (CorpseData corpse : corpses.values()) {
			CorpseAPI.removeCorpse(corpse);
		}
	}
	
	public void setFound (boolean found , Player p) {
		this.found.put(p.getUniqueId(), found);
	}
	
	public boolean isFound (Player p) {
		if (found.containsKey(p.getUniqueId())) {
			return found.get(p.getUniqueId());
		}
		return false;
	}

}
