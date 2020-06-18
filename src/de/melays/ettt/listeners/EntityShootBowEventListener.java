package de.melays.ettt.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;

public class EntityShootBowEventListener implements Listener{
	
	Main main;
	
	public EntityShootBowEventListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onEntityShootBowEvent (EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (main.getArenaManager().isInGame(p)) {
				Arena arena = main.getArenaManager().searchPlayer(p);
				if (arena.state == ArenaState.GAME) {
					arena.mapReset.addEntity(e.getProjectile());
					main.getShop().tntArrow.arrowShoot(p, (Arrow) e.getProjectile());
				}
			}
		}
	}
	
}
