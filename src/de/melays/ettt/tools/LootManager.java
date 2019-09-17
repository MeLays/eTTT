package de.melays.ettt.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.melays.ettt.Main;
import de.melays.ettt.Utf8YamlConfiguration;

public class LootManager {
	
	Main main;
	
	public LootManager(Main main) {
		this.main = main;
		
		getLootFile().options().copyDefaults(true);
		saveFile();
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public Inventory getChestInventory() {
		int items = this.getLootFile().getInt("loot.items_per_chest");
		ArrayList<String> items_to_find = new ArrayList<String>();
		for (String item : this.getLootFile().getConfigurationSection("loot.chest").getKeys(false)) {
			int amount = this.getLootFile().getInt("loot.chest."+item);
			for (int j = 0 ; j < amount ; j++) {
				items_to_find.add(item);
			}
		}
		
		Inventory inv = Bukkit.createInventory(null, 27, Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.lootchest.title")));
		
		for (int i = 0 ; i < items ; i++) {
			int slot = LootManager.randInt(0, 26);
			int item_index = LootManager.randInt(0 , items_to_find.size()-1);
			String item = items_to_find.get(item_index);
			ItemStack stack = LootManager.loadItemStack(this.getLootFile().getConfigurationSection("items."+item));
			inv.setItem(slot, stack);
		}
		
		return inv;
	}
	
	public Inventory getEnderchestInventory() {
		int items = this.getLootFile().getInt("loot.items_per_enderchest");
		ArrayList<String> items_to_find = new ArrayList<String>();
		for (String item : this.getLootFile().getConfigurationSection("loot.enderchest").getKeys(false)) {
			int amount = this.getLootFile().getInt("loot.enderchest."+item);
			for (int j = 0 ; j < amount ; j++) {
				items_to_find.add(item);
			}
		}
		
		Inventory inv = Bukkit.createInventory(null, 27, Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.enderchest.title")));
		
		for (int i = 0 ; i < items ; i++) {
			int slot = LootManager.randInt(0, 26);
			int item_index = LootManager.randInt(0 , items_to_find.size()-1);
			String item = items_to_find.get(item_index);
			ItemStack stack = LootManager.loadItemStack(this.getLootFile().getConfigurationSection("items."+item));
			inv.setItem(slot, stack);
		}
		
		return inv;
	}
	
	
	@SuppressWarnings("deprecation")
	public static ItemStack loadItemStack(ConfigurationSection data) {
		ItemStack r = new ItemStack(Material.PAPER , 1);
		try {
			r.setType(Material.getMaterial(data.getString("type").toUpperCase()));
		} catch (Exception e) {
			
		}
		try {
			r.setAmount(data.getInt("amount"));
		} catch (Exception e) {
			
		}
		try {
			r.setDurability((short) data.getInt("durability"));
		} catch (Exception e) {
			
		}
		ItemMeta meta = r.getItemMeta();
		try {
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', data.getString("displayname")));
		} catch (Exception e) {
			
		}
		try {
			List<String> lore = data.getStringList("lore");
			ArrayList<String> set = new ArrayList<String>();
			for (String s : lore) {
				set.add(ChatColor.translateAlternateColorCodes('&', s));
			}
			meta.setLore(set);
		} catch (Exception e) {
			
		}
		r.setItemMeta(meta);
		try {
			if (r.getType() == Material.POTION) {
				PotionMeta pmeta = (PotionMeta) r.getItemMeta();
				for (String s : data.getConfigurationSection("potion").getKeys(false)) {
					pmeta.setMainEffect(PotionEffectType.getByName(s));
					pmeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(s.toUpperCase()) , data.getInt("potion."+s+".time") ,  data.getInt("potion."+s+".level")), true);
				}
				r.setItemMeta(pmeta);
			}
		} catch (Exception e) {

		}
		try {
			for (String s : data.getConfigurationSection("enchantments").getKeys(false)) {
				Enchantment e = Enchantment.getByName(s.toUpperCase());
				if (e != null)
					r.addUnsafeEnchantment(e , data.getInt("enchantments."+s));
				else 
					r.addUnsafeEnchantment(EnchantmentWrapper.getByKey(NamespacedKey.minecraft(s.toUpperCase())) , data.getInt("enchantments."+s));
			}
		} catch (Exception e) {

		}
		return r;
	}
	
	//Team File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "loot.yml";
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), filenname);
	    }
	    if (!configurationFile.exists()) {
	    	main.saveResource(filenname, true);
	    }
	    configuration = new Utf8YamlConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource(filenname);
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        configuration.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getLootFile() {
	    if (configuration == null) {
	    	reloadFile();
	    }
	    return configuration;
	}
	
	public void saveFile() {
	    if (configuration == null || configurationFile == null) {
	    return;
	    }
	    try {
	        configuration.save(configurationFile);
	    } catch (IOException ex) {
	    }
	}
	
}
