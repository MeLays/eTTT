package de.melays.ettt.game.lobby;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
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
	
	//Player methods
	public void join (Player p) {
		
	}
	
	public boolean contains (Player p) {
		return players.contains(p);
	}
	
}
