package de.melays.ettt.tools;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;

public class CrackShotLayer implements Listener{
	
	CSUtility cs;
	Main main;
	
	public CrackShotLayer(Main main) {
		this.cs = new CSUtility();
		this.main = main;
	}
	
	public ItemStack getWeapon(String name) {
		return cs.generateWeapon(name);
	}
	
	@EventHandler
	public void onWeaponDamage(WeaponDamageEntityEvent e) {
		if (e.getPlayer() instanceof Player && e.getVictim() instanceof Player) {
			Player p = (Player) e.getVictim();
			Player damager = e.getPlayer();
			if (main.getArenaManager().isInGame(p)) {
				Arena arena = main.getArenaManager().searchPlayer(p);
				
				//Arena relevant Event stuff
				if (arena.state != ArenaState.GAME || arena.spectators.contains(damager)) {
					e.setCancelled(true);
				}
				else if (arena.state == ArenaState.GAME) {
					if (p.getHealth() - e.getDamage() <= 0) {
						e.setCancelled(true);
						arena.roleManager.callKill(p);
					}
				}
			}
		}
	}

}
