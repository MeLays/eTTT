package de.melays.ettt.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.Utf8YamlConfiguration;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.log.Logger;

public class SignManager {
	
	Main main;
	HashMap<Block , Sign> signs = new HashMap<Block , Sign>();
	HashMap<Block , String> signArenas = new HashMap<Block , String>();
	HashMap<Block , String> signKeys = new HashMap<Block , String>();
	
	public SignManager(Main main) {
		this.main = main;
		
		getSignFile().options().copyDefaults(true);
		saveFile();
		
		this.load();
		this.startLoop();
	}
	
	public void playerInteract(Player p , Block b) {
		if (!main.getMessageFetcher().checkPermission(p, "ttt.play")) return;
		if (!this.signArenas.containsKey(b)) return;
		String arena = this.signArenas.get(b);
		Arena a = main.getArenaManager().getArena(arena);
		if (!a.join(p)) {
			p.sendMessage(main.getMessageFetcher().getMessage("full", true));
		}
		else {
			this.updateSign(b.getLocation());
		}
	}
	
	public void addSign(Block block , String arena) {
		UUID signID = UUID.randomUUID();
		Tools.saveLiteLocation(this.getSignFile(), signID.toString(), block.getLocation());
		this.getSignFile().set(signID + ".arena", arena);
		this.saveFile();
		this.load();
	}
	
	public void startLoop() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {
				for (Block block : signArenas.keySet()) {
					updateSign(block.getLocation());
				}
			}
			
		}, 20, 20);
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
				signKeys.put(loc.getBlock() , key);
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
			Sign sign = (Sign) loc.getBlock().getState();
			String key = this.signKeys.get(loc.getBlock());
						
			if (arena == null) {
				if (!main.getArenaManager().isCreated(this.signArenas.get(loc.getBlock()))) {
					Logger.log(main.prefix + " Faulty sign found. Deleting it.");
					this.getSignFile().set(key, null);
					this.saveFile();
					this.load();
				}
				List<String> notLoaded = main.getSettingsFile().getConfiguration().getStringList("sign.not_loaded");
				sign.setLine(0, Main.c(notLoaded.get(0)));
				sign.setLine(1, Main.c(notLoaded.get(1)));
				sign.setLine(2, Main.c(notLoaded.get(2)));
				sign.setLine(3, Main.c(notLoaded.get(3)));
				sign.update();
				return;
			}
			if (arena.state == ArenaState.LOBBY) {
				if (arena.lobby.getPlayers().size() >= arena.max) {
					List<String> fullLobby = main.getSettingsFile().getConfiguration().getStringList("sign.lobby_full");
					sign.setLine(0, Main.c(fullLobby.get(0)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.setLine(1, Main.c(fullLobby.get(1)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.setLine(2, Main.c(fullLobby.get(2)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.setLine(3, Main.c(fullLobby.get(3)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.update();
				}
				else {
					List<String> lobby = main.getSettingsFile().getConfiguration().getStringList("sign.lobby");
					sign.setLine(0, Main.c(lobby.get(0)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.setLine(1, Main.c(lobby.get(1)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.setLine(2, Main.c(lobby.get(2)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.setLine(3, Main.c(lobby.get(3)
							.replace("%current%", arena.getAll().size()+"")
							.replace("%max%", arena.max + "")
							.replace("%countdown%", arena.lobby.counter + "")));
					sign.update();
				}
			}
			if (arena.state == ArenaState.WARMUP) {
				List<String> warmup = main.getSettingsFile().getConfiguration().getStringList("sign.warmup");
				sign.setLine(0, Main.c(warmup.get(0)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(1, Main.c(warmup.get(1)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(2, Main.c(warmup.get(2)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(3, Main.c(warmup.get(3)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.update();
			}
			if (arena.state == ArenaState.GAME) {
				List<String> ingame = main.getSettingsFile().getConfiguration().getStringList("sign.ingame");
				sign.setLine(0, Main.c(ingame.get(0)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(1, Main.c(ingame.get(1)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(2, Main.c(ingame.get(2)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(3, Main.c(ingame.get(3)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.update();
			}
			if (arena.state == ArenaState.END) {
				List<String> ending = main.getSettingsFile().getConfiguration().getStringList("sign.ending");
				sign.setLine(0, Main.c(ending.get(0)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(1, Main.c(ending.get(1)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(2, Main.c(ending.get(2)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.setLine(3, Main.c(ending.get(3)
						.replace("%current%", arena.getAll().size()+"")
						.replace("%max%", arena.max + "")
						.replace("%game%", arena.repeatGame + "")
						.replace("%repeated%", arena.repeatGameTotal + "")));
				sign.update();
			}
		} catch(Exception e) {
			Logger.log(main.prefix + " Faulty sign found. Deleting it.");
			this.getSignFile().set(this.signKeys.get(loc.getBlock()), null);
			this.saveFile();
			this.load();
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
