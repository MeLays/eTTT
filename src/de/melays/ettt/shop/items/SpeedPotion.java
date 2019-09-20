package de.melays.ettt.shop.items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import de.melays.ettt.Main;
import de.melays.ettt.shop.ShopItem;

public class SpeedPotion implements ShopItem{
	
	Main main;
	public SpeedPotion(Main main) {
		this.main = main;
	}

	@Override
	public void boughtItem(Player p) {
		p.getInventory().addItem(this.getPotionItemStack(PotionType.SPEED, 2, false, false, null));
	}

	@Override
	public int getPrice() {
		return main.getConfig().getInt("shop.items.speed_potion.price");
	}

	@Override
	public ItemStack getDisplayStack() {
		ItemStack stack = this.getPotionItemStack(PotionType.SPEED, 2, false, false, main.getConfig().getString("shop.items.speed_potion.display"));
		return stack;
	}
	
	public ItemStack getPotionItemStack(PotionType type, int level, boolean extend, boolean upgraded, String displayName){
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(type, extend, upgraded));
        if (displayName != null)
        	meta.setDisplayName(Main.c(displayName));
        potion.setItemMeta(meta);
        return potion;
    }

	@Override
	public boolean isEnabled() {
		return main.getConfig().getBoolean("shop.items.speed_potion.enabled");
	}

	@Override
	public List<String> getInfo() {
		return null;
	}

}
