package de.melays.ettt.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import de.melays.ettt.Main;

public class SignChangeEventListener implements Listener{
	
	Main main;
	
	public SignChangeEventListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onSignChange (SignChangeEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) return;
		if (!p.hasPermission("ttt.setup")) return;
		
		//Check for eTTT sign
		if (e.getLines()[0].equalsIgnoreCase("[eTTT]")) {
			String arena = e.getLines()[1];
			if (arena.equals("")) {
				p.sendMessage(main.getMessageFetcher().getMessage("prefix", false) + " [ERROR] "+ChatColor.DARK_RED+"You need to specify a valid arena in line 2.");
				return;
			}
			if (!main.getArenaManager().isCreated(arena)) {
				p.sendMessage(main.getMessageFetcher().getMessage("prefix", false) + " [ERROR] "+ChatColor.DARK_RED+"There is no arena using this name.");
				return;
			}
			main.getSignManager().addSign(e.getBlock(), arena);
			p.sendMessage(main.getMessageFetcher().getMessage("prefix", false) + " The sign has been added.");

		}
		
	}

}
