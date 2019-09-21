package de.melays.ettt.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.melays.ettt.Main;

public class EntityShootBowEventListener implements Listener{
	
	Main main;
	
	public EntityShootBowEventListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onEntityShootBowEvent (EntityShootBowEvent e) {
		
	}
	
}
