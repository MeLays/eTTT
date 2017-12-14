package de.melays.ettt.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.Utf8YamlConfiguration;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageFetcher {
	Main main;
	
	public MessageFetcher(Main main){
		this.main = main;
		this.reloadFile();
		this.getMessageFetcher().options().copyDefaults(true);
		this.saveMessageFile();
	}
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "messages.yml";
	
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
	
	public FileConfiguration getMessageFetcher() {
	    if (configuration == null) {
	    	reloadFile();
	    }
	    return configuration;
	}
	
	public void saveMessageFile() {
	    if (configuration == null || configurationFile == null) {
	    return;
	    }
	    try {
	        configuration.save(configurationFile);
	    } catch (IOException ex) {
	    }
	}
	
	public String getMessage (String id , boolean prefixreplace){
		String msg = getMessageFetcher().getString(id);
		if (msg != null){
			if (prefixreplace){
				msg =  msg.replace("%prefix%", getMessage("prefix" , false));
			}
			msg = msg.replace("[ae]", "ä");
			msg = msg.replace("[ue]", "ü");
			msg = msg.replace("[oe]", "ö");
			msg = msg.replace("[AE]", "Ä");
			msg = msg.replace("[UE]", "Ü");
			msg = msg.replace("[OE]", "Ö");
			return ChatColor.translateAlternateColorCodes('&',msg);
		}
		else{
			return "Your custom messages.yml doesn't contain this key ("+id+")";
		}
	}
	
	public void sendMessage(Player p , String id) {
		String msg = getMessage(id , true);
		if (!msg.equalsIgnoreCase("none")) {
			p.sendMessage(msg);
		}
	}
	
	public void sendMessage(CommandSender p , String id) {
		String msg = getMessage(id , true);
		if (!msg.equalsIgnoreCase("none")) {
			p.sendMessage(msg);
		}
	}
	
	public void sendMessage(ArrayList<Player> p , String id) {
		String msg = getMessage(id , true);
		if (!msg.equalsIgnoreCase("none")) {
			for (Player player : p) {
				player.sendMessage(msg);
			}
		}
	}
	
	public boolean checkPermission (CommandSender s , String permission) {
		if (!s.hasPermission(permission)) {
			s.sendMessage(getMessage("no_permission" , true));
			return false;
		}
		return true;
	}
}