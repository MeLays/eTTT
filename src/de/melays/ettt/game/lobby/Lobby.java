package de.melays.ettt.game.lobby;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.PlayerTools;
import de.melays.ettt.game.Arena;

public class Lobby {

	Main main;
	
	LobbyMode mode;
	Arena arena = null;
	Location lobby;
	
	//Players
	public ArrayList<Player> players = new ArrayList<Player>();
	
	public Lobby (Main main , Location loc) {
		this.main = main;
		this.lobby = loc;
	}
	
	public void setMode (LobbyMode mode) {
		this.mode = mode;
	}
	
	public void setArena (Arena arena) {
		this.arena = arena;
	}
	
	public void broadcast (String msg) {
		for (Player p : players) {
			p.sendMessage(Main.c(msg));
		}
	}
	
	//Player methods
	public void join (Player p) {
		if (!this.contains(p)) {
			players.add(p);
			PlayerTools.resetPlayer(p);
			p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.lobby").toUpperCase()));
			p.teleport(lobby);
			broadcast(main.getMessageFetcher().getMessage("game.join", true).replaceAll("%player%", p.getName()));
			
			//Give Items
			if (this.mode == LobbyMode.VOTING && main.getSettingsFile().getConfiguration().getBoolean("game.items.vote.enabled")) {
				p.getInventory().setItem(main.getSettingsFile().getConfiguration().getInt("game.items.vote.slot") , main.getItemManager().getItem("lobby.vote"));
			}
			if (main.getSettingsFile().getConfiguration().getBoolean("game.items.roleselector.enabled")) {
				p.getInventory().setItem(main.getSettingsFile().getConfiguration().getInt("game.items.roleselector.slot") , main.getItemManager().getItem("lobby.roleselector"));
			}
			if (main.getSettingsFile().getConfiguration().getBoolean("game.items.leave.enabled")) {
				p.getInventory().setItem(main.getSettingsFile().getConfiguration().getInt("game.items.leave.slot") , main.getItemManager().getItem("lobby.leave"));
			}
		}
	}
	
	public void remove (Player p) {
		PlayerTools.resetPlayer(p);
		players.remove(p);
	}
	
	public boolean contains (Player p) {
		return players.contains(p);
	}
	
}
