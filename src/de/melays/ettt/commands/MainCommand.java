package de.melays.ettt.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.tools.Tools;
import net.md_5.bungee.api.ChatColor;

public class MainCommand implements CommandExecutor {

	Main main;
	
	public MainCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		HelpSender helpSender = new HelpSender (main , alias);
		
		helpSender.addAlias("info", "All about this plugin", "See the author, website, version and more of this plugin" , "/ttt info");
		helpSender.addAlias("help [page]", "Shows this overview", "Use 'help <page>' to get to the next help pages" , "/ttt help");
		helpSender.addAlias("reload", "Reloads all files", "Reloads all files and configuration files" , "/ttt reload");
		helpSender.addAlias("create ...", "Create an arena", "Use this command to create a new arena" , "/ttt create <name> <minimal players> <maximal players>");
		helpSender.addAlias("check [arena]", "Checks the setup progress", "Checks the setup progress of an arena" , "/ttt check <arena>");
		helpSender.addAlias("setgloballobby", "Sets the lobby location", "Sets the location where you will be teleported after the game" , "/ttt setgloballobby");
		helpSender.addAlias("addspawn", "Add a player spawn", "Add a player spawn where players will spawn ingame" , "/ttt addspawn <name>");
		helpSender.addAlias("getmarkertool", "Gets the location marker tool", "Gives you an location marker tool" , "/ttt getmarkertool");
		helpSender.addAlias("savearenaarea", "Saves the selected area", "Saves the selected area of the arena" , "/ttt getmarkertool");

		
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
				return true;
			}
			int max = 0;
			try {
				max = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage(main.prefix + "'"+args[3]+"' is not a valid number!");
				return true;
			}
			String name = args[1].replaceAll("\\.", "");
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
			//String optional = ChatColor.YELLOW + "OPTIONAL";
			
			boolean canLoad = true;
			
			String globallobby = done;
			if (!main.getArenaManager().isGlobalLobbySet()) {
				globallobby = missing;
				canLoad = false;
			}
			sender.sendMessage(Main.c("   &8["+globallobby+"&8] &eSet the global lobby (/ttt setgloballobby)"));
			
			sender.sendMessage(Main.c("   &8["+done+"&8] &eCreate the arena (/ttt create)"));
			
			String spawnpoints = done;
			if (!(Tools.getLocationsCounting(main.getArenaManager().getConfiguration() , args[1].toLowerCase()+".spawns").size() >= main.getArenaManager().getConfiguration().getInt(args[1].toLowerCase()+".players.max"))) {
				spawnpoints = missing;
				canLoad = false;
			}
			sender.sendMessage(Main.c("   &8["+spawnpoints+"&8] &eAdd more spawnpoints than maximal players ["+Tools.getLocationsCounting(main.getArenaManager().getConfiguration() , args[1].toLowerCase()+".spawns").size()+" set] (/ttt addpspawn)"));
			
			String arenaarea = done;
			if (!Tools.isLocationSet(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.min") || !Tools.isLocationSet(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.max")) {
				arenaarea = missing;
				canLoad = false;
			}
			sender.sendMessage(Main.c("   &8["+arenaarea+"&8] &eSet the arena arena (/ttt savearenaarea)"));
			
			if (canLoad) {
				sender.sendMessage(Main.c("   &aThe arena is set up and is ready to be loaded!"));
			}
			else {
				sender.sendMessage(Main.c("   &cThe arena is not yet ready to be loaded!"));
			}
			
		}
		
		else if (args[0].equalsIgnoreCase("setgloballobby")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			main.getArenaManager().setGlobalLobby(p.getLocation());
			sender.sendMessage(main.prefix + " The globallobby-spawn has been set");
		}
		
		else if (args[0].equalsIgnoreCase("info")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.info") && !p.getUniqueId().equals(UUID.fromString("76cbeb4f-6ab9-489c-8665-bcc40a5654c6")))return true;
			sender.sendMessage(main.prefix + ChatColor.GOLD +" eTTT (enhanced Trouble in Terrorist Town)");
			sender.sendMessage(main.prefix + " authors: " + main.getDescription().getAuthors());
			sender.sendMessage(main.prefix + " version: " + main.getDescription().getVersion());
			sender.sendMessage(main.prefix + " website: " + main.getDescription().getWebsite());
			sender.sendMessage(main.prefix + " depend: " + main.getDescription().getDepend());
			sender.sendMessage(main.prefix + " softdepend: " + main.getDescription().getSoftDepend());
		}
		
		else if (args[0].equalsIgnoreCase("addspawn")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt addspawn <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			int id = Tools.addCounting(main.getArenaManager().getConfiguration(), args[1].toLowerCase()+".spawns" , p.getLocation());
			main.getArenaManager().saveFile();
			sender.sendMessage(main.prefix + " Added a player spawnpoint with the id '"+id+"'");
		}
		
		else if (args[0].equalsIgnoreCase("removespawn")) {
			if (!(sender instanceof Player)) return true;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt addspawn <name> <id>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			int id = 0;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				sender.sendMessage(main.prefix + " '"+args[2]+"' is not a valid number!");
				return true;
			}
			Tools.removeLocationCounting(main.getArenaManager().getConfiguration(), args[1].toLowerCase()+".spawns", id);
			main.getArenaManager().saveFile();
			sender.sendMessage(main.prefix + " If this spawnpoint existed it has been removed");
		}
		
		else if (args[0].equalsIgnoreCase("getmarkertool")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt getmarkertool"));
				return true;
			}
			main.getMarkerTool().givePlayer(p);
		}
		
		else if (args[0].equalsIgnoreCase("savearenaarea")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt savearenaarea <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			if (!main.getMarkerTool().isReady(p)) {
				p.sendMessage(main.prefix + " Please select the area using '/ttt getmarkertool'");
				return true;
			}
			Location[] locs = Tools.generateMaxMinPositions(main.getMarkerTool().get1(p), main.getMarkerTool().get2(p));
			Tools.saveLiteLocation(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.min", locs[0]);
			Tools.saveLiteLocation(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.max", locs[1]);
			main.getArenaManager().saveFile();
		}
		
		else if (args[0].equalsIgnoreCase("reload")) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.reload"))return true;
			main.reloadAll();
			sender.sendMessage(main.prefix + "Reloaded all configuration files");
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/ttt help"));
		}
		
		return true;
	}
	
}
