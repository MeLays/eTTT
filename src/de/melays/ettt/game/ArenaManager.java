package de.melays.ettt.game;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.melays.ettt.Main;
import de.melays.ettt.Utf8YamlConfiguration;
import de.melays.ettt.log.Logger;
import de.melays.ettt.tools.Tools;

public class ArenaManager {
	
	Main main;
	
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
	
	public boolean isCreated (String arena) {
		return this.getConfiguration().getKeys(false).contains(arena);
	}
	
	public boolean isLoaded (String arena) {
		return false;
	}
	
	
	//globallobby
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
