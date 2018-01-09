package de.melays.ettt.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.tools.Tools;

public class PlayerMoveEventListener implements Listener {
	
	Main main;
	
	public PlayerMoveEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerMove (PlayerMoveEvent e) {
		Player p = e.getPlayer();		
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			Location min = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), arena.name.toLowerCase() + ".arena.min");
			Location max = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), arena.name.toLowerCase() + ".arena.max");
			if (arena.spectators.contains(p)) {
				if (!Tools.isInArea(e.getTo(), min, max)) {
					p.teleport(e.getFrom());
				}
			}
			else if (arena.state == ArenaState.WARMUP || arena.state == ArenaState.END) {
				if (!Tools.isInArea(e.getTo(), min, max) && Tools.isInArea(e.getFrom(), min, max)) {
					p.teleport(e.getFrom());
				}
				else if (!Tools.isInArea(e.getTo(), min, max) && !Tools.isInArea(e.getFrom(), min, max)) {
					p.teleport(Tools.getLocationsCounting(main.getArenaManager().getConfiguration(), arena.name.toLowerCase()+".spawns").get(0));
				}
			}
			else if (arena.state == ArenaState.GAME) {
				if (!Tools.isInArea(e.getTo(), min, max)) {
					if (!main.getConfig().getBoolean("game.barrier.leave")) {
						p.teleport(e.getFrom());
					}
				}
			}
		}
	}
}
