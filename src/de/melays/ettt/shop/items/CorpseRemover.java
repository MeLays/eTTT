package de.melays.ettt.shop.items;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

import de.melays.ettt.Main;
import de.melays.ettt.shop.ShopItem;

public class CorpseRemover implements ShopItem{
	
	Main main;
	public CorpseRemover(Main main) {
		this.main = main;
	}

	@Override
	public void boughtItem(Player p) {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.traitor.items.corpse_remover.material")));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.traitor.items.corpse_remover.display")));
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
		return main.getConfig().getInt("shop.traitor.items.corpse_remover.price");
	}

	@Override
	public ItemStack getDisplayStack() {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.traitor.items.corpse_remover.material")));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.traitor.items.corpse_remover.display")));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public boolean isEnabled() {
		if (!main.addonCorpseReborn) return false;
		return main.getConfig().getBoolean("shop.traitor.items.corpse_remover.enabled");
	}

	@Override
	public List<String> getInfo() {
		return main.getConfig().getStringList("shop.traitor.items.corpse_remover.info");
	}
	
	public void use(Player p , CorpseData corpse) {
		p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
		CorpseAPI.removeCorpse(corpse);
		p.sendMessage(main.getMessageFetcher().getMessage("shop_items.corpse_remover.used", true));
	}

}
