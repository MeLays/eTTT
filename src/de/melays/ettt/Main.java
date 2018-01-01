package de.melays.ettt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.melays.ettt.commands.MainCommand;
import de.melays.ettt.commands.SetupCommand;
import de.melays.ettt.game.ArenaManager;
import de.melays.ettt.game.lobby.Lobby;
import de.melays.ettt.game.lobby.LobbyMode;
import de.melays.ettt.listeners.BlockBreakEventListener;
import de.melays.ettt.listeners.BlockPlaceEventListener;
import de.melays.ettt.listeners.EntityDamageEventListener;
import de.melays.ettt.listeners.FoodLevelChangeEventListener;
import de.melays.ettt.listeners.InventoryClickEventListener;
import de.melays.ettt.listeners.InventoryDragEventListener;
import de.melays.ettt.listeners.PlayerDropItemEventListener;
import de.melays.ettt.listeners.PlayerInteractEventListener;
import de.melays.ettt.listeners.PlayerPickupItemEventListener;
import de.melays.ettt.listeners.PlayerQuitEventListener;
import de.melays.ettt.marker.MarkerTool;
import de.melays.ettt.tools.ItemManager;
import de.melays.ettt.tools.MessageFetcher;
import de.melays.ettt.tools.SettingsFile;

public class Main extends JavaPlugin{
	
	public String prefix;
	
	//Managers
	MessageFetcher messageFetcher;
	public MessageFetcher getMessageFetcher() {
		return this.messageFetcher;
	}
	ArenaManager arenaManager;
	public ArenaManager getArenaManager() {
		return this.arenaManager;
	}
	SettingsFile settingsFile;
	public SettingsFile getSettingsFile() {
		return settingsFile;
	}
	ItemManager itemManager;
	public ItemManager getItemManager() {
		return itemManager;
	}
	
	//Tools
	MarkerTool markerTool;
	public MarkerTool getMarkerTool() {
		return markerTool;
	}
	
	//BungeeCord Lobby Object
	Lobby bungeeCordLobby;
	public Lobby getBungeeCordLobby() {
		return this.bungeeCordLobby;
	}
	
	public void onEnable() {
		
		//Initialize Configuration & Files
		this.getConfig().options().copyDefaults(true);
		this.getConfig().options().copyHeader(true);
		this.saveConfig();
		settingsFile = new SettingsFile(this);
		
		//Initialize Managers
		this.messageFetcher = new MessageFetcher(this);
		this.prefix = this.getMessageFetcher().getMessage("prefix", false);
		this.arenaManager = new ArenaManager(this);
		this.arenaManager.loadAll();
		this.itemManager = new ItemManager(this);
		
		//Initialize Tools
		this.markerTool = new MarkerTool(this);
		
		//Create BungeeCord Lobby
		try {
			this.bungeeCordLobby = new Lobby(this , this.getArenaManager().getGlobalLobby());
			this.bungeeCordLobby.setMode(LobbyMode.RANDOM);
			if (this.getConfig().getBoolean("bungeecord.voting"))
				this.bungeeCordLobby.setMode(LobbyMode.VOTING);
		} catch (Exception e) {
			
		}
		
		//Register Commands
		getCommand("tttsetup").setExecutor(new SetupCommand(this));
		getCommand("ttt").setExecutor(new MainCommand(this));
		
		//Register Events
		Bukkit.getPluginManager().registerEvents(new BlockBreakEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockPlaceEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new InventoryDragEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDropItemEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerPickupItemEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new EntityDamageEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new FoodLevelChangeEventListener(this), this);
		
		//BungeeCord Channel
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

	}
	
	public void onDisable() {
		this.getArenaManager().stopAll();
	}
	
	public static String c (String msg) {
		return (ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	public boolean isBungeeMode() {
		return this.getConfig().getBoolean("bungeecord.enabled");
	}
	
	public void reloadAll() {
		//Configuration
		this.reloadConfig();
		//MessageFetcher
		this.getMessageFetcher().reloadFile();
		//SettingsFile
		this.getSettingsFile().reloadFile();
	}
}
