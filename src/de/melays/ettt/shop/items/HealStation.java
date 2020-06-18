package de.melays.ettt.shop.items;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.shop.ShopItem;

public class HealStation implements ShopItem{
	
	Main main;
	
	HashMap<Block, Integer> heartsLeft = new HashMap<Block, Integer>();
	HashMap<Block, Arena> arenaMap = new HashMap<Block, Arena>();
	
	public HealStation(Main main) {
		this.main = main;
	}

	@Override
	public void boughtItem(Player p) {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.detective.items.heal_station.material")));
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
		return main.getConfig().getInt("shop.detective.items.heal_station.price");
	}

	@Override
	public ItemStack getDisplayStack() {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.detective.items.heal_station.material")));
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
	
	public void use(Arena arena, Player user, Block b) {
		this.heartsLeft.put(b, 35);
		this.arenaMap.put(b, arena);
		arena.mapReset.addPlacedBlock(b.getLocation(), null);
	}
	
	public void interact(Player p , Block b) {
		Arena arena = main.getArenaManager().searchPlayer(p);
		if (!this.arenaMap.containsKey(b)) return;
		if (this.arenaMap.get(b) != arena) return;
		if (p.getHealth() == p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {
			p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
			return;
		}
		double health = (p.getHealth() + 2.);
		if (health > p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) health = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
		p.setHealth(health);
		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		this.heartsLeft.put(b, this.heartsLeft.get(b) - 2);
		if (this.heartsLeft.get(b) <= 0) {
			b.setType(Material.AIR);
			this.arenaMap.remove(b);
			this.heartsLeft.remove(b);
			b.getLocation().getWorld().playSound(b.getLocation(), Sound.BLOCK_METAL_BREAK, 1, 1);
		}
	}

}
