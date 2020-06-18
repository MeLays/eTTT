package de.melays.ettt.shop.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Arrow.PickupStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
		
		boolean found = false;
		
		for (ItemStack stack : p.getInventory().getContents()) {
			if (stack.getItemMeta().getDisplayName().equals(Main.c(main.getConfig().getString("shop.traitor.items.tnt_arrows.display")))) {
				if (stack.getType() == Material.getMaterial(main.getConfig().getString("shop.traitor.items.tnt_arrows.material"))) {
					stack.setAmount(stack.getAmount() - 1);
					found = true;
					break;
				}
			}
		}
		if (!found) return;
		
		int count = 0;
		arrow.setPickupStatus(PickupStatus.DISALLOWED);
		
		int id = Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
		    @Override
		    public void run() {
		    	p.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		    	p.getWorld().playEffect(arrow.getLocation(), Effect.MOBSPAWNER_FLAMES, 4);
		    	
		    	for (Entity e : arrow.getLocation().getWorld().getNearbyEntities(arrow.getLocation(), 3.5, 3.5, 3.5)) {
		    		if (e instanceof Player) {
		    			Player p2 = ((Player) e);
		    			p2.damage(9);
		    			EntityDamageEvent event = new EntityDamageEvent(p, DamageCause.ENTITY_ATTACK, 9);
		    			p2.setLastDamageCause(event);
		    		}
		    	}
		    	
		    }
		}, 60L);
		
		counters.put(id, count);
	}

}
