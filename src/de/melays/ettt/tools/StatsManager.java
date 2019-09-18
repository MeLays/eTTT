package de.melays.ettt.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.Utf8YamlConfiguration;
import de.melays.ettt.log.Logger;
import de.melays.statsAPI.Channel;
import de.melays.statsAPI.StatsAPI;

public class StatsManager {
	
	public StatsAPI statsapi;
	Channel channel;
	
	public StatsMode mode = StatsMode.YAML;
	
	Main main;
	
	public StatsManager(Main main) {
		this.main = main;
		if (Bukkit.getPluginManager().isPluginEnabled("StatsAPI")) {
			Logger.log(main.prefix + "StatsAPI was found on this server! It will be used to store the statistics of your players!");
			statsapi = StatsAPI.getSpigotInstance();
			mode = StatsMode.STATSAPI;
			if (statsapi.isDummy()) {
				Logger.log(main.prefix + ChatColor.RED + "StatsAPI is not connected to a database server! Using the .yml file instead.");
				mode = StatsMode.YAML;
			}
			else {
				this.channel = statsapi.hookChannel(main, "bedwarsunlimited");
			}
		}
		else {
			StatsMode mode = StatsMode.valueOf(main.getConfig().getString("stats").toUpperCase());
			if (mode == null) {
				Logger.log(main.prefix + ChatColor.RED + "Unknown statsmode '" + main.getConfig().getString("stats") + "'. Using YAML ...");
				this.mode = mode = StatsMode.YAML;
				return;
			}
		}
		if (mode == StatsMode.YAML) {
			Logger.log(main.prefix + ChatColor.GOLD + "YAML files are not recommenced to store your statistics on larger servers! Use my StatsAPI instead.");
			saveFile();
		}
	}
	
	public void setKey(UUID uuid , String key , int i) {
		try {
			if (mode == StatsMode.STATSAPI) {
				channel.setKey(uuid, key, i);
			}
			else if (mode == StatsMode.YAML) {
				Logger.log(uuid + " " + key + " " + i);
				getFile().set(uuid.toString() + "." + key, i);
				this.saveFile();			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToKey (UUID uuid , String key , int add) {
		try {
			if (mode == StatsMode.STATSAPI) {
				channel.addToKey(uuid, key, add);
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setKey(uuid , key , getKey(uuid , key) + add);
	}
	
	public void setStringKey(UUID uuid , String key , String str) {
		try {
			if (mode == StatsMode.STATSAPI) {
				channel.setStringKey(uuid, key, str);
			}
			else if (mode == StatsMode.YAML) {
				getFile().set(uuid.toString() + "." + key, str);
				this.saveFile();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getKey(UUID uuid , String key) {
		try {
			if (mode == StatsMode.STATSAPI) {
				return channel.getKey(uuid, key);
			}
			else if (mode == StatsMode.YAML) {
				if (getFile().contains(uuid.toString() + "." + key))
					return getFile().getInt(uuid.toString() + "." + key);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getStringKey(UUID uuid , String key) {
		try {
			if (mode == StatsMode.STATSAPI) {
				return channel.getStringKey(uuid, key);
			}
			else if (mode == StatsMode.YAML) {
				if (getFile().contains(uuid.toString() + "." + key))
					return getFile().getString(uuid.toString() + "." + key);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void addToKarma (Player p , int amount) {
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				addToKey(p.getUniqueId(), "karma", amount);
			}
		});
	}
	
	public void addGame (Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				addToKey(p.getUniqueId(), "games", 1);
			}
		});
	}
	
	public void addWin (Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				addToKey(p.getUniqueId(), "wins", 1);
			}
		});
	}
	
	public void addLost (Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				addToKey(p.getUniqueId(), "lost", 1);
			}
		});
	}
	
	public void addPass (Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				addToKey(p.getUniqueId(), "passes", 1);
			}
		});
	}
	
	public void removePass (Player p) {
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
			@Override
			public void run() {
				addToKey(p.getUniqueId(), "passes", -1);
			}
		});
	}
	
	public int getPasses (Player p) {
		return this.getKey(p.getUniqueId(), "passes");
	}
	
	public int getKarma (Player p) {
		//Karma must ALWAYS be shown +100!
		return this.getKey(p.getUniqueId(), "karma") + 100;
	}
	
	//Team File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "stats.yml";
	
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
	
	public FileConfiguration getFile() {
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
