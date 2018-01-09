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
import org.bukkit.event.player.PlayerPickupItemEvent;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;

public class PlayerPickupItemEventListener implements Listener{

	Main main;
	
	public PlayerPickupItemEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			//Arena relevant Event stuff
			if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.END) {
				e.setCancelled(true);
			}
			else if (arena.spectators.contains(p)) {
				e.setCancelled(true);
			}
			
		}
		else if (main.getBungeeCordLobby().contains(p)) {
			e.setCancelled(true);
		}
	}
	
}
