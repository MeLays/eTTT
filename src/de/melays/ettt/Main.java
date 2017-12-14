package de.melays.ettt;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import de.melays.ettt.tools.MessageFetcher;

public class Main extends JavaPlugin{
	
	//Managers
	MessageFetcher messageFetcher;
	
	public void onEnable() {
		
		//Initialize Managers
		this.messageFetcher = new MessageFetcher(this);
		
	}
	
	public static String c (String msg) {
		return (ChatColor.translateAlternateColorCodes('&', msg));
	}
	
}
