package de.melays.ettt.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.nms.TypeOfClick;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.Role;

public class AddonCorpseClickEventListener implements Listener{
	
	Main main;
	
	public AddonCorpseClickEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onCorpseClickEvent (CorpseClickEvent e) {
		Player p = e.getClicker();
		if (!main.getArenaManager().isInGame(p)) {
			return;
		}
		e.setCancelled(true);
				
		if (e.getClickType() != TypeOfClick.RIGHT_CLICK)
			return;
		
		Arena arena = main.getArenaManager().searchPlayer(p);
		//A corpse has been clicked
		UUID playerUUID = arena.roleManager.corpseContainer.getPlayer(e.getCorpse().getEntityId());
		Player deadPlayer = Bukkit.getPlayer(playerUUID);
		
		Role oldRole = Role.INNOCENT;
		if (arena.roleManager.detectives_beginning.contains(deadPlayer.getName())) oldRole = Role.DETECTIVE;
		if (arena.roleManager.traitors_beginning.contains(deadPlayer.getName())) oldRole = Role.TRAITOR;
		
		
		if (p.getInventory().getItemInMainHand().getType() == main.getShop().defibrillator.getDisplayStack().getType()) {
			if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(main.getShop().defibrillator.getDisplayStack().getItemMeta().getDisplayName())) {
				//defibrillator used
				main.getShop().defibrillator.use(p, e.getCorpse(), deadPlayer, oldRole);
				return;
			}
		}
		
		if (p.getInventory().getItemInMainHand().getType() == main.getShop().corpseRemover.getDisplayStack().getType()) {
			if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(main.getShop().corpseRemover.getDisplayStack().getItemMeta().getDisplayName())) {
				//corpseremover used
				main.getShop().corpseRemover.use(p, e.getCorpse());
				return;
			}
		}

		if (!arena.roleManager.corpseContainer.isFound(deadPlayer) && !arena.spectators.contains(p)) {
			arena.roleManager.corpseContainer.setFound(true, deadPlayer);
			String found = main.getMessageFetcher().getMessage("addons.corpse_reborn.corpse_found", true);
			found = found.replace("%player%", p.getName());
			found = found.replace("%role%", arena.roleManager.roleToDisplayname(oldRole));
			found = found.replace("%dead_player%", deadPlayer.getName());
			arena.broadcast(found);
		}
		else {
			String found = main.getMessageFetcher().getMessage("addons.corpse_reborn.corpse_info", true);
			found = found.replace("%role%", arena.roleManager.roleToDisplayname(oldRole));
			found = found.replace("%dead_player%", deadPlayer.getName());
			p.sendMessage(found);
		}
	}

}
