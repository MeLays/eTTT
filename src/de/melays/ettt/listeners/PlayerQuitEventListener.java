package de.melays.ettt.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.melays.ettt.Main;

public class PlayerQuitEventListener implements Listener{

	Main main;
	
	public PlayerQuitEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if (main.getArenaManager().isInGame(p)) {
			main.getArenaManager().searchPlayer(p).leave(p);
		}
		else if (main.isBungeeMode() && main.getBungeeCordLobby() != null) {
			main.getBungeeCordLobby().remove(p);
		}
		
		if (!main.getConfig().getBoolean("server.join_leave_messages")) {
			e.setQuitMessage(null);
		}
		
	}
	
}
