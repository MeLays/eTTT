package de.melays.ettt.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.game.Role;

public class PlayerChatEventListener implements Listener{

	Main main;
	
	public PlayerChatEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerChat (AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		
		Arena a = main.getArenaManager().searchPlayer(p);
		
		if (a == null && main.isBungeeMode()) {
			e.setCancelled(true);
			String msg = main.getMessageFetcher().getMessage("chat.normal", true);
			msg = msg.replaceAll("%playername%", p.getName());
			msg = msg.replaceAll("%msg%", e.getMessage());
			main.getBungeeCordLobby().broadcast(msg);
		}
		
		if (!main.getArenaManager().isInGame(p)) {
			return;
		}
				
		if (a.spectators.contains(p)) {
			
			e.setCancelled(true);
			//Spectator chat
			
			for (Player p2 : a.spectators) {
				String msg = main.getMessageFetcher().getMessage("chat.spectator", true);
				msg = msg.replaceAll("%playername%", p.getName());
				msg = msg.replaceAll("%msg%", e.getMessage());
				p2.sendMessage(msg);
			}
			
			if (a.state == ArenaState.END) {
				for (Player p2 : a.getAllPlaying()) {
					String msg = main.getMessageFetcher().getMessage("chat.spectator", true);
					msg = msg.replaceAll("%playername%", p.getName());
					msg = msg.replaceAll("%msg%", e.getMessage());
					p2.sendMessage(msg);
				}
			}
			
		}
		else if (a.state == ArenaState.GAME && e.getMessage().startsWith("!")) {
			
			//Traitor chat
			
			e.setCancelled(true);
			
			if (a.roleManager.getRole(p) != Role.TRAITOR) {
				p.sendMessage(main.getMessageFetcher().getMessage("chat.error.inno_traitorchat", true));
				return;
			}
			
			for (Player p2 : a.getAllPlaying()) {
				if (a.roleManager.getRole(p2) == Role.TRAITOR) {
					String msg = main.getMessageFetcher().getMessage("chat.traitorchat", true);
					msg = msg.replaceAll("%playername%", p.getName());
					msg = msg.replaceAll("%msg%", e.getMessage().replaceFirst("!", ""));
					p2.sendMessage(msg);
				}
			}
			
		}
		else {
			
			e.setCancelled(true);
			//Normal Chat
			
			for (Player p2 : a.getAll()) {
				String msg = main.getMessageFetcher().getMessage("chat.normal", true);
				if (a.state == ArenaState.GAME)
					if (a.roleManager.getRole(p) == Role.DETECTIVE)
						msg = main.getMessageFetcher().getMessage("chat.normal_detective", true);
				msg = msg.replaceAll("%playername%", p.getName());
				msg = msg.replaceAll("%msg%", e.getMessage());
				p2.sendMessage(msg);
			}
			
		}
		
	}
	
}
