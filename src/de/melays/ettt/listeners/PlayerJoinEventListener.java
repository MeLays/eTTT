package de.melays.ettt.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.melays.ettt.Main;

public class PlayerJoinEventListener implements Listener {
	
	Main main;
	
	public PlayerJoinEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerJoin (PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if (p.hasPermission("ttt.setup")) {
			//Try catch for message
			try {
				main.getArenaManager().getGlobalLobby();
			}
			catch(Exception ex) {
				p.sendMessage(main.getMessageFetcher().getMessage("prefix", false) + " [ERROR] "+ChatColor.DARK_RED+"No global lobby set. This is needed for the plugin to work. You may encounter errors in console.");
				return;
			}
		}
		
		if (main.isBungeeMode() && main.getBungeeCordLobby() != null && main.current == null) {
			main.getBungeeCordLobby().join(p);
		}
		else if (main.isBungeeMode() && main.current != null) {
			main.current.join(p);
		}
		
		if (!main.getConfig().getBoolean("server.join_leave_messages")) {
			e.setJoinMessage(null);
		}
		
	}
}
