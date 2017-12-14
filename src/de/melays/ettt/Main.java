package de.melays.ettt;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.melays.ettt.commands.MainCommand;
import de.melays.ettt.game.ArenaManager;
import de.melays.ettt.tools.MessageFetcher;

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
	
	public void onEnable() {
		
		//Initialize Config
		this.getConfig().options().copyDefaults(true);
		this.getConfig().options().copyHeader(true);
		this.saveConfig();
		
		//Initialize Managers
		this.messageFetcher = new MessageFetcher(this);
		this.prefix = this.getMessageFetcher().getMessage("prefix", false);
		this.arenaManager = new ArenaManager(this);
		
		//Register Commands
		getCommand("ttt").setExecutor(new MainCommand(this));
		
	}
	
	public static String c (String msg) {
		return (ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	public void reloadAll() {
		//Config
		this.reloadConfig();
		//MessageFetcher
		this.getMessageFetcher().reloadFile();
	}
}
