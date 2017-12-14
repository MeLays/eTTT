package de.melays.ettt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.melays.ettt.Main;
import net.md_5.bungee.api.ChatColor;

public class MainCommand implements CommandExecutor {

	Main main;
	
	public MainCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		HelpSender helpSender = new HelpSender (main , alias);
		
		helpSender.addAlias("help [page]", "Shows this overview", "Use 'help <page>' to get to the next help pages" , "/ttt help");
		
		if (args.length == 0) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.help"))return true;
			helpSender.sendHelp(sender, 1);
		}
		
		else if (args[0].equalsIgnoreCase("help")) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.help"))return true;
			if (args.length == 1) {
				helpSender.sendHelp(sender, 1);
			}
			else {
				try {
					int page = Integer.parseInt(args[1]);
					helpSender.sendHelp(sender, page);
				} catch (NumberFormatException e) {
					helpSender.sendHelp(sender, 1);
				}
			}
		}
		
		//Create Arena
		else if (args[0].equalsIgnoreCase("create")) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 4) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt create <name> <minimal players> <maximal players>"));
				return true;
			}
			int min = 0;
			try {
				min = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage(main.prefix + "'"+args[2]+"' is not a valid number!");
			}
			int max = 0;
			try {
				max = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage(main.prefix + "'"+args[3]+"' is not a valid number!");
			}
			String name = args[1].replaceAll(".", "");
			if (name.length() == 0) name = "empty";
			String result = main.getArenaManager().createArena(name, min, max);
			if (result == null) {
				sender.sendMessage(main.prefix + "Arena '"+name+"' has been created. Use '/ttt check "+name+"' to see how to continue setting up your arena.");
			}
			else {
				sender.sendMessage(main.prefix + "Error: " + ChatColor.RED + result);
			}
		}
		
		//Check Arena
		else if (args[0].equalsIgnoreCase("check")) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt check <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			sender.sendMessage(main.prefix + " Arena '"+args[1].toLowerCase()+"' setup process:");
			
			String done = ChatColor.GREEN + "DONE";
			String missing = ChatColor.RED + "MISSING";
			String optional = ChatColor.YELLOW + "OPTIONAL";
			
			String globallobby = missing;
			//TODO
			sender.sendMessage(Main.c("   &8["+globallobby+"&8] &7Set the global lobby (/ttt setgloballobby)"));
			
			sender.sendMessage(Main.c("   &8["+done+"&8] &7Create the arena (/ttt create)"));
			
		}
		
		else if (args[0].equalsIgnoreCase("reload")) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.reload"))return true;
			main.reloadAll();
			sender.sendMessage(main.prefix + "Reloaded all configuration files.");
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/ttt help"));
		}
		
		return true;
	}
	
}
