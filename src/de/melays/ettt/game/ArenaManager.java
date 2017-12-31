package de.melays.ettt.game;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.Utf8YamlConfiguration;
import de.melays.ettt.log.Logger;
import de.melays.ettt.tools.Tools;

public class ArenaManager {
	
	Main main;
	
	//Arena Hashmap
	HashMap<String , Arena> arenas = new HashMap<String , Arena>();
	
	public ArenaManager (Main main) {
		this.main = main;
		this.getConfiguration().options().copyDefaults(true);
		this.saveFile();
		Logger.log(main.prefix + " [ArenaManager] Loading...");
	}
	
	public String createArena (String name , int min , int max) {
		String display = name;
		name = name.toLowerCase();
		if (this.getConfiguration().getKeys(false).contains(name)) return "This arena already exists!";
		if (min < 2) return "The minimal amount of players has to be 2 or more!";
		if (max < min) return "This minimal amount of players has to be smaller than the maximal amount of players!";
		
		this.getConfiguration().set(name+".display", display);
		this.getConfiguration().set(name+".players.min", min);
		this.getConfiguration().set(name+".players.max", max);
		this.saveFile();
		
		Logger.log(main.prefix + " [ArenaManager] Created arena '"+name+"'");
		
		return null;
	}
	
	public void stopAll() {
		for (Arena a : this.arenas.values()) {
			a.stop();
		}
	}
	
	void unregister(Arena a) {
		arenas.remove(a.name.toLowerCase());
	}
	
	//Player Methods
	
	public boolean isInGame(Player p) {
		for (Arena a : arenas.values()) {
			if (a.contains(p)) return true;
		}
		return false;
	}
	
	public Arena searchPlayer(Player p) {
		for (Arena a : arenas.values()) {
			if (a.contains(p)) return a;
		}
		return null;
	}
	
	//LOAD/RELOAD/STOP Methods
	
	public void loadAll() {
		Logger.log(main.prefix + " [ArenaManager] (Re)loading all Arenas ...");
		int c = 0;
		for (String s : this.getConfiguration().getKeys(false)) {
			if (load(s)) c++;
		}
		Logger.log(main.prefix + " [ArenaManager] Loaded " + c + " arenas.");
	}
	
	public boolean load(String arena) {
		if (isLoaded(arena)) {
			return false;
		}
		if (!canLoad(arena)) {
			return false;
		}
		arenas.put(arena.toLowerCase(), new Arena(main , arena));
		return true;
	}
	
	public boolean unload (String arena) {
		if (!isLoaded(arena)) {
			return false;
		}
		this.getArena(arena).stop();
		arenas.remove(arena.toLowerCase());
		return true;
	}
	
	public boolean reload (String arena) {
		unload(arena);
		return load(arena);
	}
	
	public boolean isLoaded(String arena) {
		return arenas.containsKey(arena.toLowerCase());
	}
	
	public boolean canLoad (String arena) {
		boolean canLoad = true;
		if (!main.isBungeeMode()) {
			if (!Tools.isLocationSet(main.getArenaManager().getConfiguration(), arena.toLowerCase()+".lobby")) {
				canLoad = false;
			}
		}
		if (!this.isGlobalLobbySet()) {
			canLoad = false;
		}
		if (!(Tools.getLocationsCounting(this.getConfiguration() , arena.toLowerCase()+".spawns").size() >= this.getConfiguration().getInt(arena.toLowerCase()+".players.max"))) {
			canLoad = false;
		}
		if (!Tools.isLocationSet(this.getConfiguration(), arena.toLowerCase() + ".arena.min") || !Tools.isLocationSet(this.getConfiguration(), arena.toLowerCase() + ".arena.max")) {
			canLoad = false;
		}
		return canLoad;
	}
	//--------
	
	public Arena getArena(String arena) {
		if (this.isLoaded(arena)) {
			return arenas.get(arena.toLowerCase());
		}
		return null;
	}
	
	public boolean isCreated (String arena) {
		return this.getConfiguration().getKeys(false).contains(arena);
	}
	
	//Global Lobby
	public void setGlobalLobby(Location loc) {
		Tools.saveLocation(main.getSettingsFile().getConfiguration(), "global_lobby", loc);
		main.getSettingsFile().saveFile();
	}
	
	public boolean isGlobalLobbySet () {
		return Tools.isLocationSet(main.getSettingsFile().getConfiguration(), "global_lobby");
	}
	
	public Location getGlobalLobby () {
		return Tools.getLocation(main.getSettingsFile().getConfiguration(), "global_lobby");
	}
	
	//Arena File Management
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "arenas.yml";
	
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
	
	public FileConfiguration getConfiguration() {
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
