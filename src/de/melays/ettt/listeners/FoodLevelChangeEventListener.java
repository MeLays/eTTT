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
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;

public class FoodLevelChangeEventListener implements Listener{

	Main main;
	
	public FoodLevelChangeEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.END || !main.getConfig().getBoolean("game.hunger")) {
				e.setFoodLevel(20);
				e.setCancelled(true);
			}
		}
	}
	
}
