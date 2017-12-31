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
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import de.melays.ettt.Main;

public class InventoryDragEventListener implements Listener{

	Main main;
	
	public InventoryDragEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryDragEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (main.getArenaManager().isInGame(p)) {
			if (e.getInventory().getType() == InventoryType.CRAFTING) {
				e.setCancelled(true);
			}
		}
	}
	
}
