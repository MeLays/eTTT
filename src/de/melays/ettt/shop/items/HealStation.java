package de.melays.ettt.shop.items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ettt.Main;
import de.melays.ettt.shop.ShopItem;

public class HealStation implements ShopItem{
	
	Main main;
	public HealStation(Main main) {
		this.main = main;
	}

	@Override
	public void boughtItem(Player p) {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.detective.items.heal_station.material")));
		stack.setAmount(main.getConfig().getInt("shop.detective.items.heal_station.amount"));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.detective.items.heal_station.display")));
		List<String> info = this.getInfo();
		for (int i = 0 ; i < info.size() ; i++) {
			info.set(i, Main.c(info.get(i)));
		}
		meta.setLore(info);
		stack.setItemMeta(meta);
		p.getInventory().addItem(stack);
	}

	@Override
	public int getPrice() {
		return main.getConfig().getInt("shop.items.corpse_remover.price");
	}

	@Override
	public ItemStack getDisplayStack() {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.detective.items.heal_station.material")));
		stack.setAmount(main.getConfig().getInt("shop.detective.items.heal_station.amount"));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.detective.items.heal_station.display")));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public boolean isEnabled() {
		return main.getConfig().getBoolean("shop.detective.items.heal_station.enabled");
	}

	@Override
	public List<String> getInfo() {
		return main.getConfig().getStringList("shop.detective.items.heal_station.info");
	}

}
