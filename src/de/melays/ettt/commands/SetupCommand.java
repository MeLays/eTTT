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

public class SetupCommand implements CommandExecutor {

	Main main;
	
	public SetupCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		HelpSender helpSender = new HelpSender (main , alias);
		
		helpSender.addAlias("info", "All about this plugin", "See the author, website, version and more of this plugin" , "/ttt-setup info");
		helpSender.addAlias("help [page]", "Shows this overview", "Use 'help <page>' to get to the next help pages" , "/ttt-setup help");
		helpSender.addAlias("reload", "Reloads all files", "Reloads all files and configuration files" , "/ttt-setup reload");
		helpSender.addAlias("create ...", "Create an arena", "Use this command to create a new arena" , "/ttt-setup create <name> <minimal players> <maximal players>");
		helpSender.addAlias("check <arena>", "Checks the setup progress", "Checks the setup progress of an arena" , "/ttt-setup check <arena>");
		helpSender.addAlias("setgloballobby <arena>", "Sets the lobby location", "Sets the location where you will be teleported after the game" , "/ttt-setup setgloballobby");
		helpSender.addAlias("setlobby <arena>", "Sets the arena lobby", "Sets the arena lobby\n&cNot neccessarry in BungeeCord-mode" , "/ttt-setup setlobby <name>");
		helpSender.addAlias("setspectatorspawn <arena>", "Sets the spectator spawn", "Sets the arena spectator spawn" , "/ttt-setup setspectatorspawn <name>");
		helpSender.addAlias("addspawn <arena>", "Add a player spawn", "Add a player spawn where players will spawn ingame" , "/ttt-setup addspawn <name>");
		helpSender.addAlias("removespawn <arena> <id>", "Remove a player spawn", "Removes a player spawn" , "/ttt-setup removespawn <name> <id>");
		helpSender.addAlias("getmarkertool", "Gets the location marker tool", "Gives you an location marker tool" , "/ttt-setup getmarkertool");
		helpSender.addAlias("savearenaarea <arena>", "Saves the selected area", "Saves the selected area of the arena" , "/ttt-setup savearenaarea <name>");
		helpSender.addAlias("leave", "Leaves a game", "Leaves a game (works in bungee-mode)" , "/ttt-setup leave");
		helpSender.addAlias("load", "Loads an arena", "Loads an arena" , "/ttt-setup load <name>");
		helpSender.addAlias("unload", "Unloads an arena", "Unloads an arena" , "/ttt-setup unload <name>");
		helpSender.addAlias("reload", "Reloads an arena", "Reloads an arena" , "/ttt-setup reload <name>");
		
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
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup create <name> <minimal players> <maximal players>"));
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
				sender.sendMessage(main.prefix + "Arena '"+name+"' has been created. Use '/ttt-setup check "+name+"' to see how to continue setting up your arena.");
			}
			else {
				sender.sendMessage(main.prefix + "Error: " + ChatColor.RED + result);
			}
		}
		
		//Check Arena
		else if (args[0].equalsIgnoreCase("check")) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup check <name>"));
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
			sender.sendMessage(Main.c("   &8["+globallobby+"&8] &eSet the global lobby (/ttt-setup setgloballobby)"));
			
			sender.sendMessage(Main.c("   &8["+done+"&8] &eCreate the arena (/ttt-setup create)"));
			
			if (!main.isBungeeMode()) {
				String lobbyarena = done;
				if (!main.isBungeeMode()) {
					if (!Tools.isLocationSet(main.getArenaManager().getConfiguration(), args[1].toLowerCase()+".lobby")) {
						canLoad = false;
						lobbyarena = missing;
					}
					sender.sendMessage(Main.c("   &8["+lobbyarena+"&8] &eSet the arena lobby (/ttt-setup setlobby)"));
				}
			}
			
			String spectatorspawn = done;
			if (!main.isBungeeMode()) {
				if (!Tools.isLocationSet(main.getArenaManager().getConfiguration(), args[1].toLowerCase()+".spectator")) {
					canLoad = false;
					spectatorspawn = missing;
				}
				sender.sendMessage(Main.c("   &8["+spectatorspawn+"&8] &eSet the spectatorspawn (/ttt-setup setspectatorspawn)"));
			}
			
			String spawnpoints = done;
			if (!(Tools.getLocationsCounting(main.getArenaManager().getConfiguration() , args[1].toLowerCase()+".spawns").size() >= main.getArenaManager().getConfiguration().getInt(args[1].toLowerCase()+".players.max"))) {
				spawnpoints = missing;
				canLoad = false;
			}
			sender.sendMessage(Main.c("   &8["+spawnpoints+"&8] &eAdd more spawnpoints than maximal players ["+Tools.getLocationsCounting(main.getArenaManager().getConfiguration() , args[1].toLowerCase()+".spawns").size()+" set] (/ttt-setup addspawn)"));
			
			String arenaarea = done;
			if (!Tools.isLocationSet(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.min") || !Tools.isLocationSet(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.max")) {
				arenaarea = missing;
				canLoad = false;
			}
			sender.sendMessage(Main.c("   &8["+arenaarea+"&8] &eSet the arena arena (/ttt-setup savearenaarea)"));
			
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
		
		else if (args[0].equalsIgnoreCase("leave")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (main.getBungeeCordLobby().contains(p)) main.getBungeeCordLobby().remove(p);
			if (main.getArenaManager().isInGame(p)) main.getArenaManager().searchPlayer(p).leave(p);
			sender.sendMessage(main.prefix + " You have left all arenas without leaving the server.");
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
		
		else if (args[0].equalsIgnoreCase("setlobby")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup addspawn <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			Tools.saveLocation(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".lobby", p.getLocation());
			main.getArenaManager().saveFile();
			sender.sendMessage(main.prefix + " The lobby location has been saved");
			if (main.isBungeeMode())
				sender.sendMessage(main.prefix + " TIP: In BungeeCord-mode the lobby location is not neccessary");
		}
		
		else if (args[0].equalsIgnoreCase("setspectatorspawn")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup setspectatorspawn <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			Tools.saveLocation(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".spectator", p.getLocation());
			main.getArenaManager().saveFile();
			sender.sendMessage(main.prefix + " The spectator location has been saved");
		}
		
		else if (args[0].equalsIgnoreCase("addspawn")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup addspawn <name>"));
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
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup addspawn <name> <id>"));
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
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup getmarkertool"));
				return true;
			}
			main.getMarkerTool().givePlayer(p);
		}
		
		else if (args[0].equalsIgnoreCase("savearenaarea")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup savearenaarea <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			if (!main.getMarkerTool().isReady(p)) {
				p.sendMessage(main.prefix + " Please select the area using '/ttt-setup getmarkertool'");
				return true;
			}
			Location[] locs = Tools.generateMaxMinPositions(main.getMarkerTool().get1(p), main.getMarkerTool().get2(p));
			Tools.saveLiteLocation(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.min", locs[0]);
			Tools.saveLiteLocation(main.getArenaManager().getConfiguration(), args[1].toLowerCase() + ".arena.max", locs[1]);
			main.getArenaManager().saveFile();
			sender.sendMessage(main.prefix + " The area has been saved!");
		}
		
		else if (args[0].equalsIgnoreCase("load")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup load <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			try {
				if (main.getArenaManager().isLoaded(args[1].toLowerCase())) {
					p.sendMessage(main.prefix + " This arena has already been loaded. Use /ttt-setup arenareload");
					return true;				
				}
				if (!main.getArenaManager().canLoad(args[1].toLowerCase())) {
					p.sendMessage(main.prefix + " This arena cannot be loaded. Please check using /ttt-setup check " + args[1].toLowerCase());
					return true;				
				}
				main.getArenaManager().load(args[1].toLowerCase());
				if (main.getArenaManager().isLoaded(args[1].toLowerCase())) {
					p.sendMessage(main.prefix + " The arena has been successfully loaded.");
					return true;				
				}
			}finally {
				
			}
			p.sendMessage(main.prefix + " An error occurred loading this arena.");
			return true;				
		}
		
		else if (args[0].equalsIgnoreCase("unload")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup unload <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			try {
				if (!main.getArenaManager().isLoaded(args[1].toLowerCase())) {
					p.sendMessage(main.prefix + " This arena is not loaded. Use /ttt-setup load");
					return true;				
				}
				main.getArenaManager().unload(args[1].toLowerCase());
				if (!main.getArenaManager().isLoaded(args[1].toLowerCase())) {
					p.sendMessage(main.prefix + " The arena has been successfully unloaded.");
					return true;				
				}
			}finally {
				
			}
			p.sendMessage(main.prefix + " An error occurred unloading this arena.");
			return true;				
		}
		
		else if (args[0].equalsIgnoreCase("arenareload")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.setup"))return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt-setup arenareload <name>"));
				return true;
			}
			if (!main.getArenaManager().isCreated(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			try {
				if (!main.getArenaManager().canLoad(args[1].toLowerCase())) {
					p.sendMessage(main.prefix + " This arena cannot be loaded. Please check using /ttt-setup check " + args[1].toLowerCase());
					return true;				
				}
				if (main.getArenaManager().isLoaded(args[1].toLowerCase()))
					main.getArenaManager().unload(args[1].toLowerCase());
				main.getArenaManager().load(args[1].toLowerCase());
				if (main.getArenaManager().isLoaded(args[1].toLowerCase())) {
					p.sendMessage(main.prefix + " The arena has been successfully reloaded.");
					return true;				
				}
			}finally {
				
			}
			p.sendMessage(main.prefix + " An error occurred reloading this arena.");
			return true;
		}
		
		else if (args[0].equalsIgnoreCase("reload")) {
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.reload"))return true;
			main.reloadAll();
			sender.sendMessage(main.prefix + "Reloaded all configuration files");
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/ttt-setup help"));
		}
		
		return true;
	}
	
}
