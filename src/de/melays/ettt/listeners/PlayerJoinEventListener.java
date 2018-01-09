package de.melays.ettt.listeners;

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
		
		if (main.isBungeeMode() && main.getBungeeCordLobby() != null) {
			main.getBungeeCordLobby().join(p);
		}
		
		if (!main.getConfig().getBoolean("server.join_leave_messages")) {
			e.setJoinMessage(null);
		}
		
	}
}
