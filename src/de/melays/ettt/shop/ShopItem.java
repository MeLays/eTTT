package de.melays.ettt.shop;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ShopItem {

	public int getPrice();
	
	public ItemStack getDisplayStack();
	
	public void boughtItem(Player p);
	
	public boolean isEnabled();
	
	public List<String> getInfo();

}
