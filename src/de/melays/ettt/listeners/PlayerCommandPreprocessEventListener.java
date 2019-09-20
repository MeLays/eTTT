package de.melays.ettt.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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
		if (arena.state == ArenaState.GAME) {
			if (command.equals(main.getConfig().getString("game.shop_command").toLowerCase())){
				if (arena.roleManager.getRole(p) != Role.INNOCENT) {
					main.getShop().openShop(p, arena);
					e.setCancelled(true);
					return;
				}
				p.sendMessage(main.getMessageFetcher().getMessage("shop_items.cannot_use_shop", true));
				e.setCancelled(true);
			}
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
