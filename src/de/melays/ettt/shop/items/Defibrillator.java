package de.melays.ettt.shop.items;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

import de.melays.ettt.Main;
import de.melays.ettt.PlayerTools;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaScoreboard;
import de.melays.ettt.game.Role;
import de.melays.ettt.shop.ShopItem;

public class Defibrillator implements ShopItem{
	
	Main main;
	public Defibrillator(Main main) {
		this.main = main;
	}

	@Override
	public void boughtItem(Player p) {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.items.defibrillator.material")));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.items.defibrillator.display")));
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
		return main.getConfig().getInt("shop.items.defibrillator.price");
	}

	@Override
	public ItemStack getDisplayStack() {
		ItemStack stack = new ItemStack(Material.getMaterial(main.getConfig().getString("shop.items.defibrillator.material")));
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(Main.c(main.getConfig().getString("shop.items.defibrillator.display")));
		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public boolean isEnabled() {
		if (!main.addonCorpseReborn) return false;
		return main.getConfig().getBoolean("shop.items.defibrillator.enabled");
	}

	@Override
	public List<String> getInfo() {
		return main.getConfig().getStringList("shop.items.defibrillator.info");
	}
	
	public void use(Player p , CorpseData corpse , Player revive , Role role) {
		Arena arena = main.getArenaManager().searchPlayer(p);
		if (!arena.spectators.contains(revive)) {
			p.sendMessage(main.getMessageFetcher().getMessage("shop_items.defibrillator.left", true));
			return;
		}
		p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
		arena.spectators.remove(revive);
		if (role == Role.TRAITOR) {
			arena.roleManager.setRole(revive, role);
		}
		PlayerTools.resetPlayer(p);
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.game").toUpperCase()));
		ArenaScoreboard.createPlayerScoreboard(arena, p);
		arena.updateAll();
		arena.updateVisibility();
		revive.teleport(p);
		CorpseAPI.removeCorpse(corpse);
		p.sendMessage(main.getMessageFetcher().getMessage("shop_items.defibrillator.used", true).replace("%player%", revive.getName()));
	}

}
