/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;

public class BlockPlaceEventListener implements Listener{

	Main main;
	
	public BlockPlaceEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)  || main.getBungeeCordLobby().contains(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			if (arena.state != ArenaState.GAME) return;
			if (e.getBlock().getType() == Material.getMaterial(main.getConfig().getString("shop.detective.items.heal_station.material"))) {
				if (e.getItemInHand().getItemMeta().getDisplayName().equals(Main.c(main.getConfig().getString("shop.detective.items.heal_station.display")))) {
					main.getShop().healStation.use(arena, p, e.getBlock());
					return;
				}
			}
			
			e.setCancelled(true);
		}
	}
	
}
