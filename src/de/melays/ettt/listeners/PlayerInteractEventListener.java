/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
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
import de.melays.ettt.game.Role;
import de.melays.ettt.game.lobby.LobbyMode;

public class PlayerInteractEventListener implements Listener{

	Main main;
	
	public PlayerInteractEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		//Sign interact
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block b = e.getClickedBlock();
			BlockState blockState = b.getState();
			if (blockState instanceof Sign) {
				main.getSignManager().playerInteract(p, b);
				p.getInventory().setHeldItemSlot(1);
				return;
			}
		}
		
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			if (e.getAction() == Action.PHYSICAL) {
				//Protect farmland
			    if(e.getClickedBlock().getType() == Material.FARMLAND)
			        e.setCancelled(true);
			    else if (main.getConfig().getBoolean("game.interact.pressure_plates")) {
					e.setCancelled(true);
				}
				return;
			}
			
			
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
				else if (arena.state == ArenaState.GAME || arena.state == ArenaState.WARMUP && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (e.getClickedBlock() != null) {
						if (e.getClickedBlock().getType() == Material.CHEST) {
							if (!arena.chests.containsKey(e.getClickedBlock().getLocation())) {
								arena.chests.put(e.getClickedBlock().getLocation(), main.getLootManager().getChestInventory());
							}
							p.openInventory(arena.chests.get(e.getClickedBlock().getLocation()));
							e.setCancelled(true);
						}
						else if (e.getClickedBlock().getType() == Material.ENDER_CHEST && arena.state != ArenaState.WARMUP) {
							if (!arena.chests.containsKey(e.getClickedBlock().getLocation())) {
								arena.chests.put(e.getClickedBlock().getLocation(), main.getLootManager().getEnderchestInventory());
							}
							p.openInventory(arena.chests.get(e.getClickedBlock().getLocation()));
							e.setCancelled(true);
						}
						else if (!main.getConfig().getStringList("game.interact.blocks").contains(e.getClickedBlock().getType().toString())) {
							if (p.getInventory().getItemInMainHand() != null) {
								String item_in_hand = p.getInventory().getItemInMainHand().getType().toString();
								if ((p.getInventory().getItemInMainHand().getType().toString().contains("POTION") && main.getConfig().getBoolean("game.interact.potions")) ||
										(p.getInventory().getItemInMainHand().getType() == Material.BOW && main.getConfig().getBoolean("game.interact.bow"))) {
									
								}
								else if ((item_in_hand.contains("CHESTPLATE") || item_in_hand.contains("HELMET") || item_in_hand.contains("LEGGINGS") || item_in_hand.contains("BOOTS"))
										&& main.getConfig().getBoolean("game.interact.armor")) {
									
								}
								else if (arena.roleManager.getRole(p) == Role.TRAITOR && main.getConfig().getStringList("game.interact.only_traitor").contains(e.getClickedBlock().getType().toString())) {
									
								}
								else {
									e.setCancelled(true);
								}
							}
							else {
								e.setCancelled(true);
							}
						}
					}
					else {
						e.setCancelled(false);
					}					
					
				}
				else if (arena.state == ArenaState.END) {
					e.setCancelled(true);
				}
			}finally {
				
			}
		}
		else if (main.getBungeeCordLobby() != null) {
			if (main.getBungeeCordLobby().contains(p)) {
				e.setCancelled(true);
				//Item Interact Check
				if ((e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK 
						|| e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getItem() != null) {
					if (main.getItemManager().isItem("lobby.roleselector", e.getItem())) {
						main.getBungeeCordLobby().roleMenus.get(p).open();
					}
					else if (main.getItemManager().isItem("lobby.vote", e.getItem())) {
						if (main.getBungeeCordLobby().mode == LobbyMode.VOTING) {
							main.getBungeeCordLobby().voteManager.openInventory(p);
						}
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
}
