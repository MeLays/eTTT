package de.melays.ettt.tools;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.melays.ettt.Main;

public class InventorySaver {
	
	Main main;
	
	HashMap<Player , ItemStack[]> playerInventory = new HashMap<Player , ItemStack[]>();
	HashMap<Player , ItemStack[]> playerArmorInventory = new HashMap<Player , ItemStack[]>();
	HashMap<Player , ItemStack[]> playerExtraInventory = new HashMap<Player , ItemStack[]>();
	HashMap<Player , Integer> playerLevel = new HashMap<Player , Integer>();

	public InventorySaver(Main main) {
		this.main = main;
	}

	public void saveInventory (Player p) {
		if (main.isBungeeMode()) return;
		this.playerInventory.put(p, p.getInventory().getContents());
		this.playerArmorInventory.put(p, p.getInventory().getArmorContents());
		this.playerExtraInventory.put(p, p.getInventory().getExtraContents());
		this.playerLevel.put(p , p.getLevel());
	}
	
	public void restoreInventory(Player p) {
		if (main.isBungeeMode()) return;
		p.getInventory().setContents(this.playerInventory.get(p));
		p.getInventory().setArmorContents(this.playerArmorInventory.get(p));
		p.getInventory().setExtraContents(this.playerExtraInventory.get(p));
		p.setLevel(this.playerLevel.get(p));
	}
	
}
