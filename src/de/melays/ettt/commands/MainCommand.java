package de.melays.ettt.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.ettt.Main;

public class MainCommand implements CommandExecutor {

	Main main;
	
	public MainCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
	
		if (args.length == 0) {
			sendHelp(sender);
		}
		
		else if (args[0].equalsIgnoreCase("help")) {
			sendHelp(sender);
		}
		
		else if (args[0].equalsIgnoreCase("join")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.play")) return true;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt join <arena>"));
				return true;
			}
			if (!main.getArenaManager().isLoaded(args[1].toLowerCase())) {
				sender.sendMessage(main.getMessageFetcher().getMessage("unknown_arena", true));
				return true;				
			}
			if (main.getArenaManager().isInGame(p)) {
				sender.sendMessage(main.getMessageFetcher().getMessage("already_ingame", true));
				return true;		
			}
			if (!main.getArenaManager().getArena(args[1].toLowerCase()).join(p)) {
				sender.sendMessage(main.getMessageFetcher().getMessage("full", true));
				return true;		
			}
		}
		
		else if (args[0].equalsIgnoreCase("leave")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.play"))return true;
			if (args.length != 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt leave"));
				return true;
			}
			if (main.isBungeeMode()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(main.getConfig().getString("bungeecord.lobbyserver"));
				p.sendPluginMessage(main, "BungeeCord", out.toByteArray());
				return true;
			}
			if (!main.getArenaManager().isInGame(p)) {
				sender.sendMessage(main.getMessageFetcher().getMessage("not_ingame", true));
				return true;		
			}
			main.getArenaManager().searchPlayer(p).leave(p);
		}
		
		else if (args[0].equalsIgnoreCase("stats")) {
			if (!(sender instanceof Player)) return true;
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "ttt.play"))return true;
			if (args.length <= 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/ttt stats [Player]"));
				return true;
			}
			String about = null;
			if (args.length >= 2) {
				about = args[1];
			}
			main.getStatsManager().sendStatsMessage(p, about);
		}

		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/ttt help"));
		}
		
		return true;
	}
	
	public void sendHelp (CommandSender sender) {
		for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("user-help")) {
			sender.sendMessage(Main.c(s).replaceAll("%prefix%", main.prefix));
		}
	}
	
}
