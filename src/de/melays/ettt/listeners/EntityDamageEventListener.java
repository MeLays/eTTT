/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;

public class EntityDamageEventListener implements Listener{

	Main main;
	
	public EntityDamageEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (main.getArenaManager().isInGame(p)) {
				Arena arena = main.getArenaManager().searchPlayer(p);
				
				//Arena relevant Event stuff
				if (arena.state == ArenaState.LOBBY) {
					e.setCancelled(true);
				}

			}
		}
	}
	
}
