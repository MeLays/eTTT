package de.melays.ettt.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import de.melays.ettt.Main;

public class EntityRegainHealthEventListener implements Listener{

	Main main;
	
	public EntityRegainHealthEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onEntityRegainHealthEvent(EntityRegainHealthEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		if (!main.getArenaManager().isInGame(p)) {
			return;
		}
		if (main.getConfig().getBoolean("game.regenerate")) {
			return;
		}
		if (e.getRegainReason() == RegainReason.REGEN) {
			e.setCancelled(true);
		}
		else if (e.getRegainReason() == RegainReason.SATIATED) {
			e.setCancelled(true);
		}
	}
	
}
