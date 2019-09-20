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
import org.bukkit.event.inventory.InventoryClickEvent;
import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.game.lobby.LobbyMode;

public class InventoryClickEventListener implements Listener{

	Main main;
	
	public InventoryClickEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			if (arena.state == ArenaState.LOBBY) {
				e.setCancelled(true);
				
				if (e.getView().getTitle().equals(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.roleselection.title")))) {
					arena.lobby.roleMenus.get(p).click(e.getSlot());
				}
			}	
			if (arena.state == ArenaState.GAME) {
				if (e.getView().getTitle().equals(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.shop.detective_title")))) {
					main.getShop().clickDetectiveshop(p, e.getSlot());
					e.setCancelled(true);
				}
				else if (e.getView().getTitle().equals(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.shop.traitor_title")))) {
					main.getShop().clickTraitorshop(p, e.getSlot());
					e.setCancelled(true);
				}
			}
		}
		else if (main.getBungeeCordLobby().contains(p)) {
			e.setCancelled(true);
			
			if (e.getView().getTitle().equals(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.roleselection.title")))) {
				main.getBungeeCordLobby().roleMenus.get(p).click(e.getSlot());
			}
			else if (e.getView().getTitle().equals(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.title")))) {
				if (main.getBungeeCordLobby().mode == LobbyMode.VOTING) {
					main.getBungeeCordLobby().voteManager.click(p , e.getSlot());
				}
			}
		}
			
	}
	
}
