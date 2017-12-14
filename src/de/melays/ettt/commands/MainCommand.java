package de.melays.ettt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.melays.ettt.Main;

public class MainCommand implements CommandExecutor {

	Main main;
	
	public MainCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		HelpSender helpSender = new HelpSender (main , alias);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/bw help");
		helpSender.addAlias("cluster [...]", "Get to the cluster management overview", "Shows commands to manage the clusters.\nUse 'cluster <page>' to get to the next help pages" , "/bw cluster");
		helpSender.addAlias("setup [...]", "Get to the cluster-setup management overview", "Shows commands to manage the clusters locations.\nUse 'setup <page>' to get to the next help pages" , "/bw setup");
		helpSender.addAlias("arenas [...]", "Get to the arena management overview", "Shows commands to manage the arenas.\nManage the running arenas.\nUse 'arenas <page>' to get to the next help pages" , "/bw arenas");
		helpSender.addAlias("teams [...]", "Get to the team management overview", "Shows commands to manage the teams.\nUse 'teams <page>' to get to the next help pages" , "/bw teams");
		helpSender.addAlias("lobby [...]", "Get to the lobby management overview", "Shows commands to manage the lobby.\nUse 'lobby <page>' to get to the next help pages" , "/bw lobby");
		helpSender.addAlias("reload", "Reload the configuration files", "Reloades all configuration files.\nThis can cause issues in running arenas!" , "/bw reload");
		helpSender.addAlias("worldtp <game , presets , world>", "Teleport to a world", "You can teleport to those worlds:\n - 'GAME', here are the arenas generated to\n - 'PRESETS', here you can create presets\\n - 'WORLD', the specified default world" , "/bw worldtp <game , presets , world>");
		helpSender.addAlias("colorlist", "List all colors", "Lists all available Colors to create teams" , "/bw colorlist");
		helpSender.addAlias("showsettings", "Lists the settings of a player", "Lists all settings from this player" , "/bw showsettings <player>");

		
		if (args.length == 0) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return true;
			helpSender.sendHelp(sender, 1);
		}
		
		else if (args[0].equalsIgnoreCase("help")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return true;
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
		
		else if (args[0].equalsIgnoreCase("reload")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.reload"))return true;
			main.reloadAll();
			sender.sendMessage(main.prefix + "Reloaded all configuration files.");
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/bw help"));
		}
		
		return true;
	}
	
}
