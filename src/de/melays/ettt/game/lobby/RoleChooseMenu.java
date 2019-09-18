package de.melays.ettt.game.lobby;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ettt.Main;
import de.melays.ettt.game.Role;

public class RoleChooseMenu {
	
	Main main;
	Lobby lobby;
	Player p;
	
	public RoleChooseMenu (Main main , Lobby lobby , Player p) {
		this.main = main;
		this.p = p;
		this.lobby = lobby;
	}
	
	public void open () {
		
		if (main.getStatsManager().getPasses(p) <= 0) {
			p.sendMessage(main.getMessageFetcher().getMessage("points.cant_open_menu", true));
			return;
		}
		
		Inventory inv = Bukkit.createInventory(null, 18, Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.roleselection.title")));
		
		inv.setItem(10, main.getItemManager().getItem("lobby.inventory.roleselection.none"));
		inv.setItem(13, main.getItemManager().getItem("lobby.inventory.roleselection.detective"));
		inv.setItem(16, main.getItemManager().getItem("lobby.inventory.roleselection.traitor"));
		
		for (int i = 0 ; i < 9 ; i++) {
			inv.setItem(i, main.getItemManager().getItem("spacer"));
		}
		
		ItemStack selected = main.getItemManager().getItem("lobby.inventory.roleselection.selected");

		if (lobby.rolePackage.getRole(p.getUniqueId()) == null || lobby.rolePackage.getRole(p.getUniqueId()) == Role.INNOCENT) {
			selected.setType(main.getItemManager().getItem("lobby.inventory.roleselection.none").getType());
			ItemMeta meta = selected.getItemMeta();
			meta.setDisplayName(meta.getDisplayName().replaceAll("%role%", Main.c(main.getSettingsFile().getConfiguration().getString("roles.none"))));
			selected.setItemMeta(meta);
		}
		
		else if (lobby.rolePackage.getRole(p.getUniqueId()) == Role.TRAITOR) {
			selected.setType(main.getItemManager().getItem("lobby.inventory.roleselection.traitor").getType());
			ItemMeta meta = selected.getItemMeta();
			meta.setDisplayName(meta.getDisplayName().replaceAll("%role%", Main.c(main.getSettingsFile().getConfiguration().getString("roles.traitor.display"))));
			selected.setItemMeta(meta);
		}
		
		else if (lobby.rolePackage.getRole(p.getUniqueId()) == Role.DETECTIVE) {
			selected.setType(main.getItemManager().getItem("lobby.inventory.roleselection.detective").getType());
			ItemMeta meta = selected.getItemMeta();
			meta.setDisplayName(meta.getDisplayName().replaceAll("%role%", Main.c(main.getSettingsFile().getConfiguration().getString("roles.detective.display"))));
			selected.setItemMeta(meta);
		}
		
		inv.setItem(4, selected);
		p.openInventory(inv);
	}
	
	public void click (int slot) {
		if (slot == 10) lobby.rolePackage.setRequest(p.getUniqueId(), Role.INNOCENT);
		if (slot == 13) lobby.rolePackage.setRequest(p.getUniqueId(), Role.DETECTIVE);
		if (slot == 16) lobby.rolePackage.setRequest(p.getUniqueId(), Role.TRAITOR);
		open();
	}
	
}
