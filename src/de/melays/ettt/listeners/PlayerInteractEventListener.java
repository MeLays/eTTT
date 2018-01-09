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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;

public class PlayerInteractEventListener implements Listener{

	Main main;
	
	public PlayerInteractEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			try {
				//Arena relevant Event stuff
				if (arena.spectators.contains(p)) {
					e.setCancelled(true);
				}
				if (arena.state == ArenaState.LOBBY) {
					e.setCancelled(true);
					//Item Interact Check
					if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK 
							|| e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
						if (main.getItemManager().isItem("lobby.roleselector", e.getItem())) {
							arena.lobby.roleMenus.get(p).open();
						}
						else if (main.getItemManager().isItem("lobby.leave", e.getItem())) {
							if (main.isBungeeMode()) {
								ByteArrayDataOutput out = ByteStreams.newDataOutput();
								out.writeUTF("Connect");
								out.writeUTF(main.getConfig().getString("bungeecord.lobbyserver"));
								p.sendPluginMessage(main, "BungeeCord", out.toByteArray());
							}
							else {
								arena.leave(p);
							}
						}
					}
				}
			}finally {
				
			}
		}
		else if (main.getBungeeCordLobby().contains(p)) {
			e.setCancelled(true);
			//Item Interact Check
			if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK 
					|| e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
				if (main.getItemManager().isItem("lobby.roleselector", e.getItem())) {
					main.getBungeeCordLobby().roleMenus.get(p).open();
				}
				else if (main.getItemManager().isItem("lobby.leave", e.getItem())) {
					if (main.isBungeeMode()) {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Connect");
						out.writeUTF(main.getConfig().getString("bungeecord.lobbyserver"));
						p.sendPluginMessage(main, "BungeeCord", out.toByteArray());
					}
				}
			}
		}
	}
	
}
