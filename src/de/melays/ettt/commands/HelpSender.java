package de.melays.ettt.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class HelpSender {
	
	String command;
	Main main;
	
	public HelpSender (Main main , String command) {
		this.main = main;
		this.command = command;
	}
	
	HashMap<String , String> short_descr = new HashMap<String , String>();
	HashMap<String , String> descr = new HashMap<String , String>();
	HashMap<String , String> click = new HashMap<String , String>();
	ArrayList<String> alias = new ArrayList<String>();
	
	int page = 8;

	public void addAlias (String alias , String short_descr, String descr , String click) {
		this.short_descr.put(alias, short_descr);
		this.descr.put(alias, descr);
		this.click.put(alias, click);
		this.alias.add(alias);
	}
	
	public void sendHelp (CommandSender sender, int page) {
		
		int max_pages = (descr.size() / this.page) + 1; 
		if (page > max_pages || page <= 0) {
			page = 1;
		}
		
		String header = main.getMessageFetcher().getMessage("help.header" , true);
		header = header.replaceAll("%command%", command);
		header = header.replaceAll("%page%", page + "");
		header = header.replaceAll("%max_pages%", max_pages + "");
		
		sender.sendMessage(header);
		
		ArrayList<String> send = new ArrayList<String>();
		for (int i = (page-1) * this.page ; i < page * this.page ; i++) {
			if (alias.size() - 1 >= i) {
				send.add(alias.get(i));
			}
			else {
				sender.sendMessage("");
			}
		}
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			for (String s : send) {
				String scommand = main.getMessageFetcher().getMessage("help.command" , true);
				scommand = scommand.replaceAll("%alias%", s);
				scommand = scommand.replaceAll("%short_descr%", short_descr.get(s));
				TextComponent command = new TextComponent(TextComponent.fromLegacyText(scommand));
				
				String htop = main.getMessageFetcher().getMessage("help.hover_top" , true).replaceAll("%command%", this.command).replaceAll("%alias%", s);
				ComponentBuilder hover = new ComponentBuilder(htop + "\n");
				
				String body = main.getMessageFetcher().getMessage("help.hover_body_color" , true) + descr.get(s);
				body = body.replaceAll("\n", "\n" + main.getMessageFetcher().getMessage("help.hover_body_color" , true));
				
				hover.append(body);
				command.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover.create()));
				command.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click.get(s)));
				
				p.spigot().sendMessage(command);
			}
		}
		else {
			for (String s : send) {
				String command = main.getMessageFetcher().getMessage("help.command" , true);
				command = command.replaceAll("%alias%", s);
				command = command.replaceAll("%short_descr%", short_descr.get(s));
				sender.sendMessage(command);
			}
		}
		
		String footer = main.getMessageFetcher().getMessage("help.footer" , true);
		footer = footer.replaceAll("%command%", command);
		footer = footer.replaceAll("%page%", page + "");
		footer = footer.replaceAll("%max_pages%", max_pages + "");
		
		sender.sendMessage(footer);

	}
	
}
