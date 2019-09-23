package de.melays.ettt.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.game.Role;

public class PlayerCommandPreprocessEventListener implements Listener{

	Main main;
	
	public PlayerCommandPreprocessEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		Arena arena = main.getArenaManager().searchPlayer(p);
		if (arena == null) {
			return;
		}
		
		String[] cmds = e.getMessage().split(" ");
		String command = cmds[0].replaceAll("/", "");
		command = command.toLowerCase();
		
		//Shop Command
		if (command.equals(main.getConfig().getString("game.shop_command").toLowerCase())){
			if (arena.state != ArenaState.GAME) {
				p.sendMessage(main.getMessageFetcher().getMessage("not_ingame", true));
				e.setCancelled(true);
				return;
			}
			if (arena.roleManager.getRole(p) != Role.INNOCENT) {
				main.getShop().openShop(p, arena);
				e.setCancelled(true);
				return;
			}
			p.sendMessage(main.getMessageFetcher().getMessage("shop_items.cannot_use_shop", true));
			e.setCancelled(true);
		}
		
		//Traitorpass Command
		else if (command.equals(main.getConfig().getString("game.traitorpass_command").toLowerCase())){
			if (arena.state != ArenaState.LOBBY && arena.state != ArenaState.WARMUP) {
				p.sendMessage(main.getMessageFetcher().getMessage("only_in_lobby_or_warmup", true));
				e.setCancelled(true);
				return;
			}
			if (main.getStatsManager().getPasses(p) <= 0) {
				p.sendMessage(main.getMessageFetcher().getMessage("command.no_passes", true));
				e.setCancelled(true);
				return;
			}
			p.sendMessage(main.getMessageFetcher().getMessage("command.pass_used", true));
			arena.lobby.rolePackage.setRequest(p.getUniqueId(), Role.TRAITOR);
			e.setCancelled(true);
		}
		
		//Leave Command
		else if (command.equals(main.getConfig().getString("game.leave_command").toLowerCase())){
			if (main.isBungeeMode()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(main.getConfig().getString("bungeecord.lobbyserver"));
				p.sendPluginMessage(main, "BungeeCord", out.toByteArray());
			}
			else {
				arena.leave(p);
			}
			e.setCancelled(true);
		}
		
		//Start Command
		else if (command.equals(main.getConfig().getString("game.start_command").toLowerCase())){
			if (!p.hasPermission("ttt.start")) {
				p.sendMessage(main.getMessageFetcher().getMessage("no_permission", true));
				return;
			}
			if (arena.state != ArenaState.LOBBY) {
				p.sendMessage(main.getMessageFetcher().getMessage("only_in_lobby", true));
				e.setCancelled(true);
				return;
			}
			boolean startEarly = arena.lobby.startEarly();
			if (!startEarly) {
				p.sendMessage(main.getMessageFetcher().getMessage("command.start_failed", true));
			}
			else {
				p.sendMessage(main.getMessageFetcher().getMessage("command.start", true));
			}
			e.setCancelled(true);
			return;
		}
		
		//Start Command
		else if (command.equals(main.getConfig().getString("game.stats_command").toLowerCase())){
			if (!p.hasPermission("ttt.play")) {
				p.sendMessage(main.getMessageFetcher().getMessage("no_permission", true));
				return;
			}
			String about = null;
			if (cmds.length == 2) {
				about = cmds[1];
			}
			main.getStatsManager().sendStatsMessage(p, about);
			e.setCancelled(true);
			return;
		}
		
		//Leave Command
		else if (command.equals(main.getConfig().getString("game.detectivepass_command").toLowerCase())){
			if (arena.state != ArenaState.LOBBY && arena.state != ArenaState.WARMUP) {
				p.sendMessage(main.getMessageFetcher().getMessage("only_in_lobby_or_warmup", true));
				e.setCancelled(true);
				return;
			}
			if (main.getStatsManager().getPasses(p) <= 0) {
				p.sendMessage(main.getMessageFetcher().getMessage("command.no_passes", true));
				e.setCancelled(true);
				return;
			}
			p.sendMessage(main.getMessageFetcher().getMessage("command.pass_used", true));
			arena.lobby.rolePackage.setRequest(p.getUniqueId(), Role.DETECTIVE);
			e.setCancelled(true);
		}
		
		if (p.hasPermission("ttt.allow_commands")) {
			return;
		}

		if (main.getConfig().getStringList("game.allowed_commands").contains(command)) {
			return;
		}
		e.setCancelled(true);
		p.sendMessage(main.getMessageFetcher().getMessage("game.forbidden_command", true));
	}
	
}
