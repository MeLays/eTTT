package de.melays.ettt.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.melays.ettt.Main;
import de.melays.ettt.Utf8YamlConfiguration;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.log.Logger;

public class SignManager {
	
	Main main;
	HashMap<Block , Sign> signs = new HashMap<Block , Sign>();
	HashMap<Block , String> signArenas = new HashMap<Block , String>();
	
	public SignManager(Main main) {
		this.main = main;
		
		getSignFile().options().copyDefaults(true);
		saveFile();
		
		this.load();
	}
	
	public void load() {
		signs = new HashMap<Block,Sign>();
		signArenas = new HashMap<Block , String>();
		for (String key : this.getSignFile().getKeys(false)) {
			try {
				Location loc = Tools.getLiteLocation(this.getSignFile(), key);
				Sign signBlock = (Sign) loc.getBlock().getState();
				String arena = this.getSignFile().getString(key + ".arena");
				
				signs.put(loc.getBlock(), signBlock);
				signArenas.put(loc.getBlock(), arena);
			} catch(Exception e) {
				Logger.log(main.prefix + " Faulty sign found. Deleting it.");
				this.getSignFile().set(key, null);
				this.saveFile();
			}
		}
		Logger.log(main.prefix + " [SignManager] Successully loaded " + signs.size() + " signs.");
	}
	
	public void updateSign(Location loc) {
		try {
			Arena arena = main.getArenaManager().getArena(this.signArenas.get(loc.getBlock()));
			Sign sign = this.signs.get(loc.getBlock());
			if (arena.state == ArenaState.LOBBY) {
				if (arena.lobby.getPlayers().size() >= arena.max) {
					List<String> fullLobby = main.getSettingsFile().getConfiguration().getStringList("sign.lobby_full");
					sign.setLine(0, fullLobby.get(0)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + ""));
					sign.setLine(1, fullLobby.get(1)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + ""));
					sign.setLine(2, fullLobby.get(2)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + ""));
					sign.setLine(3, fullLobby.get(3)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + ""));
				}
			}
		} catch(Exception e) {
			Logger.log(main.prefix + " Faulty sign found. Skipping it...");
		}
	}
	
	//Sign File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "signs.yml";
	
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
	
	public FileConfiguration getSignFile() {
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
