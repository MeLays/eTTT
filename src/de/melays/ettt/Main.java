package de.melays.ettt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;

import de.melays.ettt.commands.MainCommand;
import de.melays.ettt.commands.SetupCommand;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaManager;
import de.melays.ettt.game.lobby.Lobby;
import de.melays.ettt.game.lobby.LobbyMode;
import de.melays.ettt.listeners.AddonCorpseClickEventListener;
import de.melays.ettt.listeners.BlockBreakEventListener;
import de.melays.ettt.listeners.BlockPlaceEventListener;
import de.melays.ettt.listeners.EntityDamageByEntityEventListener;
import de.melays.ettt.listeners.EntityDamageEventListener;
import de.melays.ettt.listeners.EntityRegainHealthEventListener;
import de.melays.ettt.listeners.FoodLevelChangeEventListener;
import de.melays.ettt.listeners.InventoryClickEventListener;
import de.melays.ettt.listeners.InventoryDragEventListener;
import de.melays.ettt.listeners.PlayerChatEventListener;
import de.melays.ettt.listeners.PlayerCommandPreprocessEventListener;
import de.melays.ettt.listeners.PlayerDropItemEventListener;
import de.melays.ettt.listeners.PlayerInteractEventListener;
import de.melays.ettt.listeners.PlayerJoinEventListener;
import de.melays.ettt.listeners.PlayerMoveEventListener;
import de.melays.ettt.listeners.PlayerPickupItemEventListener;
import de.melays.ettt.listeners.PlayerQuitEventListener;
import de.melays.ettt.log.Logger;
import de.melays.ettt.marker.MarkerTool;
import de.melays.ettt.shop.Shop;
import de.melays.ettt.tools.InventorySaver;
import de.melays.ettt.tools.ItemManager;
import de.melays.ettt.tools.LootManager;
import de.melays.ettt.tools.MessageFetcher;
import de.melays.ettt.tools.SettingsFile;
import de.melays.ettt.tools.StatsManager;

public class Main extends JavaPlugin{
	
	public String prefix;
	
	//Addon plugins found and enabled
	public boolean addonCorpseReborn = false;
	
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
	LootManager lootManager;
	public LootManager getLootManager() {
		return lootManager;
	}
	StatsManager statsManager;
	public StatsManager getStatsManager() {
		return statsManager;
	}
	InventorySaver inventorySaver;
	public InventorySaver getInventorySaver() {
		return inventorySaver;
	}
	Shop shop;
	public Shop getShop() {
		return shop;
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
	
	public Arena current;
	
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
		this.itemManager = new ItemManager(this);
		this.lootManager = new LootManager(this);
		this.statsManager = new StatsManager(this);
		this.inventorySaver = new InventorySaver(this);
		
		this.arenaManager.loadAll();

		//Initialize Tools
		this.markerTool = new MarkerTool(this);
		
		//Create BungeeCord Lobby
		resetBungeeLobby();
		
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
		Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new FoodLevelChangeEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMoveEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerChatEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new EntityRegainHealthEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessEventListener(this), this);

		//Search for Addons
		if (Bukkit.getPluginManager().isPluginEnabled("CorpseReborn") && this.getConfig().getBoolean("addons.corpse_reborn.enabled")) {
			Logger.log(this.prefix + " [Addons] Hooking into CorpseReborn (you can disable this in the config) ...");
			this.addonCorpseReborn = true;
			try {
				@SuppressWarnings("unused")
				Class<CorpseAPI> x = CorpseAPI.class;
			}catch(Exception ex){
				ex.printStackTrace();
				Logger.log(this.prefix + " [Addons] Hooking failed.");
				this.addonCorpseReborn = false;
			}
			Logger.log(this.prefix + " [Addons] Hooking done.");
			Logger.log(this.prefix + " [Addons] NOTE: If you only installed CorpseReborn for this plugin, you can disable 'on-death' in the config of CorpseReborn so the plugin wont spawn corpses everytime someone dies on your server.");

			//Register Events
			Bukkit.getPluginManager().registerEvents(new AddonCorpseClickEventListener(this), this);
		}
		
		//Shop must load after all addons
		this.shop = new Shop(this);

		//BungeeCord Channel
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		if (this.isBungeeMode()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				this.getBungeeCordLobby().join(p);
			}
		}

	}
	
	public void resetBungeeLobby() {
		if (this.bungeeCordLobby != null) {
			this.bungeeCordLobby.destroy();
		}
		try {
			this.bungeeCordLobby = new Lobby(this , this.getArenaManager().getGlobalLobby());
			this.bungeeCordLobby.setMode(LobbyMode.RANDOM);
			if (this.getConfig().getBoolean("bungeecord.voting"))
				this.bungeeCordLobby.setMode(LobbyMode.VOTING);
			this.bungeeCordLobby.startLoop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onDisable() {
		if (this.addonCorpseReborn) {
			CorpseAPI.removeAllCorpses();
		}
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
