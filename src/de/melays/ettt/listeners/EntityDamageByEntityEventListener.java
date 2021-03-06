/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt.listeners;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.tools.Tools;

public class EntityDamageByEntityEventListener implements Listener{

	Main main;
	
	public EntityDamageByEntityEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			if (main.getArenaManager().isInGame(p)) {
				Arena arena = main.getArenaManager().searchPlayer(p);
				
				//Arena relevant Event stuff
				if (arena.state != ArenaState.GAME || arena.spectators.contains(damager)) {
					e.setCancelled(true);
				}
			}
		}
		else if (e.getEntity() instanceof ItemFrame) {
			for (Arena arena : main.getArenaManager().arenas.values()) {
				Location min = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), arena.name.toLowerCase() + ".arena.min");
				Location max = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), arena.name.toLowerCase() + ".arena.max");
				if (Tools.isInArea(e.getEntity().getLocation(), min, max)) {
					e.setCancelled(true);
					break;
				}
			}
		}
	}
	
}
