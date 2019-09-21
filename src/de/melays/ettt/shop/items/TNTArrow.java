package de.melays.ettt.shop.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ettt.Main;
import de.melays.ettt.shop.ShopItem;

public class TNTArrow implements ShopItem{
	
	public ArrayList<Arrow> currentlyFlying = new ArrayList<Arrow>();
	
	HashMap<Integer , Integer> counters = new HashMap<Integer, Integer>();
	
	Main main;
	public TNTArrow(Main main) {
		this.main = main;
	}

	@Override
	public void boughtItem(Player p) {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.traitor.items.tnt_arrows.material")));
		stack.setAmount(main.getConfig().getInt("shop.traitor.items.tnt_arrows.amount"));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.traitor.items.tnt_arrows.display")));
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
		return main.getConfig().getInt("shop.traitor.items.tnt_arrows.price");
	}

	@Override
	public ItemStack getDisplayStack() {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.traitor.items.tnt_arrows.material")));
		stack.setAmount(main.getConfig().getInt("shop.traitor.items.tnt_arrows.amount"));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.traitor.items.tnt_arrows.display")));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public boolean isEnabled() {
		return main.getConfig().getBoolean("shop.traitor.items.tnt_arrows.enabled");
	}

	@Override
	public List<String> getInfo() {
		return main.getConfig().getStringList("shop.traitor.items.tnt_arrows.info");
	}
	
	public void arrowShoot(Player p, Arrow arrow) {
		
		int count = 0;
		
		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
		    @Override
		    public void run() {
		    	p.getWorld().playEffect(p.getLocation(), Effect.BLAZE_SHOOT, 10);
		    }
		}, 1L, 2L);
		
		counters.put(id, count);
	}

}
