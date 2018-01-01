package de.melays.ettt.game;

import java.util.HashMap;
import java.util.UUID;

public class RolePackage {
	
	public HashMap<UUID , Role> choice = new HashMap<UUID , Role>();
	
	public RolePackage() {
		
	}
	
	public void setRequest (UUID uuid , Role role) {
		if (role == Role.INNOCENT) {
			choice.put(uuid, null);
			choice.remove(uuid);
		}
		choice.put(uuid, role);
	}
	
	public Role getRole (UUID uuid) {
		if (!choice.containsKey(uuid)) return null;
		return choice.get(uuid);
	}
	
}
