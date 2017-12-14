package de.melays.ettt.commands;

import java.util.ArrayList;
import org.bukkit.command.CommandSender;

import de.melays.ettt.Main;

public class ListSender {
	
	String name;
	Main main;
	
	public ListSender (Main main , String name) {
		this.main = main;
		this.name = name;
	}
	
	ArrayList<String> entry = new ArrayList<String>();
	
	int page = 8;

	public void addItem (String entry) {
		this.entry.add(entry);
	}
	
	public void sendList (CommandSender sender, int page) {
		
		int max_pages = (entry.size() / this.page) + 1; 
		if (page > max_pages || page <= 0) {
			page = 1;
		}
		
		String header = main.getMessageFetcher().getMessage("list.header" , true);
		header = header.replaceAll("%list%", name);
		header = header.replaceAll("%page%", page + "");
		header = header.replaceAll("%max_pages%", max_pages + "");
		
		sender.sendMessage(header);
		
		ArrayList<String> send = new ArrayList<String>();
		for (int i = (page-1) * this.page ; i < page * this.page ; i++) {
			if (entry.size() - 1 >= i) {
				send.add(entry.get(i));
			}
			else {
				sender.sendMessage("");
			}
		}
		
		for (String s : send) {
			String command = main.getMessageFetcher().getMessage("list.command" , true);
			command = command.replaceAll("%entry%", s);
			sender.sendMessage(command);
		}
		
		String footer = main.getMessageFetcher().getMessage("list.footer" , true);
		footer = footer.replaceAll("%list%", name);
		footer = footer.replaceAll("%page%", page + "");
		footer = footer.replaceAll("%max_pages%", max_pages + "");
		
		sender.sendMessage(footer);

	}
	
}
