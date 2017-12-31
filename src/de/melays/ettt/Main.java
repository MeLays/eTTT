package de.melays.ettt;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.melays.ettt.commands.SetupCommand;
import de.melays.ettt.game.ArenaManager;
import de.melays.ettt.game.lobby.Lobby;
import de.melays.ettt.game.lobby.LobbyMode;
import de.melays.ettt.marker.MarkerTool;
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
		
		//Initialize Tools
		this.markerTool = new MarkerTool(this);
		
		//Create BungeeCord Lobby
		this.bungeeCordLobby = new Lobby(this , this.getArenaManager().getGlobalLobby());
		this.bungeeCordLobby.setMode(LobbyMode.RANDOM);
		if (this.getConfig().getBoolean("bungeecord.voting"))
			this.bungeeCordLobby.setMode(LobbyMode.VOTING);
		
		//Register Commands
		getCommand("tttsetup").setExecutor(new SetupCommand(this));
		
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
