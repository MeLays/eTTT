package de.melays.ettt.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.Role;
import de.melays.ettt.log.Logger;
import de.melays.ettt.shop.items.CorpseRemover;
import de.melays.ettt.shop.items.Defibrillator;
import de.melays.ettt.shop.items.HealStation;
import de.melays.ettt.shop.items.HealthPotion;
import de.melays.ettt.shop.items.SpeedPotion;
import de.melays.ettt.shop.items.TNTArrow;

public class Shop {
	
	Main main;
	
	SpeedPotion speedPotion;
	HealthPotion healthPotion;
	Defibrillator defibrillator;
	TNTArrow tntArrow;
	CorpseRemover corpseRemover;
	HealStation healStation;
	
	HashMap<ShopItem , Integer> slots_traitor = new HashMap<ShopItem , Integer>();
	HashMap<ShopItem , Integer> slots_detective = new HashMap<ShopItem , Integer>();
	
	ArrayList<ShopItem> items = new ArrayList<ShopItem>();
	ArrayList<ShopItem> traitor_items = new ArrayList<ShopItem>();
	ArrayList<ShopItem> detective_items = new ArrayList<ShopItem>();

	public Shop(Main main) {
		this.main = main;

		this.speedPotion = new SpeedPotion(main);
		this.healthPotion = new HealthPotion(main);
		this.defibrillator = new Defibrillator(main);
		this.tntArrow = new TNTArrow(main);
		this.corpseRemover = new CorpseRemover(main);
		this.healStation = new HealStation(main);
		
		//Calculate slots
		int current_slot = 0;
		if (this.speedPotion.isEnabled()) {
			this.slots_detective.put(this.speedPotion , current_slot);
			this.slots_traitor.put(this.speedPotion , current_slot);
			items.add(this.speedPotion);
			
			current_slot ++;
		}
		if (this.healthPotion.isEnabled()) {
			this.slots_detective.put(this.healthPotion , current_slot);
			this.slots_traitor.put(this.healthPotion , current_slot);
			items.add(this.healthPotion);

			current_slot ++;
		}
		if (this.defibrillator.isEnabled()) {
			this.slots_detective.put(this.defibrillator , current_slot);
			this.slots_traitor.put(this.defibrillator , current_slot);
			items.add(this.defibrillator);

			current_slot ++;
		}
		
		int current_slot_traitor = current_slot;
		int current_slot_detective = current_slot;

		if (this.tntArrow.isEnabled()) {
			this.slots_traitor.put(this.tntArrow , current_slot_traitor);
			this.traitor_items.add(this.tntArrow);
			
			current_slot_traitor ++;
		}
		if (this.corpseRemover.isEnabled()) {
			this.slots_traitor.put(this.corpseRemover , current_slot_traitor);
			this.traitor_items.add(this.corpseRemover);

			current_slot_traitor ++;
		}
		
		if (this.healStation.isEnabled()) {
			this.slots_detective.put(this.healStation , current_slot_detective);
			this.detective_items.add(this.healStation);
			
			current_slot_detective ++;
		}
		
		Logger.log(main.prefix + " [Shop] Loaded " + this.items.size() + " item(s) , " + this.traitor_items.size() + " traitoritem(s) and " + this.detective_items.size() + " detectiveitem(s).");
	}
	
	public void openShop(Player p, Arena arena) {
		Inventory inv;
		if (arena.roleManager.getRole(p) == Role.DETECTIVE) {
			inv = Bukkit.createInventory(null, 8, Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.shop.detective_shop")));
		}
		else if (arena.roleManager.getRole(p) == Role.TRAITOR){
			 inv = Bukkit.createInventory(null, 8, Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.shop.traitor_title")));
		}
		else {
			return;
		}
		
		for (ShopItem item : this.items) {
			inv.addItem(addPriceLore(item));
		}
		
		if (arena.roleManager.getRole(p) == Role.DETECTIVE) {
			for (ShopItem item : this.detective_items) {
				inv.addItem(addPriceLore(item));
			}
		}
		else if (arena.roleManager.getRole(p) == Role.TRAITOR) {
			for (ShopItem item : this.traitor_items) {
				inv.addItem(addPriceLore(item));
			}
		}
		
		p.openInventory(inv);
	}
	
	public ItemStack addPriceLore(ShopItem item) {
		ItemStack stack = item.getDisplayStack();
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore ==  null)
			lore = new ArrayList<String>();
		ArrayList<String> new_lore = new ArrayList<String>();
		new_lore.add(Main.c(main.getConfig().getString("shop.price_lore")));
		new_lore.addAll(lore);
		meta.setLore(new_lore);
		stack.setItemMeta(meta);
		return stack;
	}

}
