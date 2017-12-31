package de.melays.ettt.game;

import java.util.HashMap;
import java.util.UUID;

public class RolePackage {
	
	public HashMap<UUID , Role> choice = new HashMap<UUID , Role>();
	
	public RolePackage() {
		
	}
	
	public void setRequest (UUID uuid , Role role) {
		if (role == Role.INNOCENT) {
			choice.remove(uuid);
		}
		choice.put(uuid, role);
	}
	
}
