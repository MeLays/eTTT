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
import org.bukkit.event.block.BlockBreakEvent;
import de.melays.ettt.Main;

public class BlockBreakEventListener implements Listener{

	Main main;
	
	public BlockBreakEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p) || main.getBungeeCordLobby().contains(p)) {
			//Arena arena = main.getArenaManager().searchPlayer(p);
			
			e.setCancelled(true);
			
		}
	}
	
}
