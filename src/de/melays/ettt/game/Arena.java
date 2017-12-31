package de.melays.ettt.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.game.lobby.Lobby;
import de.melays.ettt.game.lobby.LobbyMode;
import de.melays.ettt.log.Logger;
import de.melays.ettt.tools.Tools;

public class Arena {
	
	//Main Instance
	Main main;
	
	//Data
	String name;
	String display;
	
	//Lobby
	Lobby lobby;
	
	//State
	ArenaState state = ArenaState.LOBBY;
	
	//Players
	ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Player> spectators = new ArrayList<Player>();
	
	public Arena (Main main , String name) {
		this.main = main;
		
		Logger.log(main.prefix + " [Arena (name="+name+")] Loading...");
		
		this.name = name;
		this.display = main.getArenaManager().getConfiguration().getString(name+".display");
		this.lobby = new Lobby(main , Tools.getLocation(main.getArenaManager().getConfiguration(), name + ".lobby"));
		this.lobby.setMode(LobbyMode.FIXED);
		this.lobby.setArena(this);
	}
	
	public ArrayList<Player> getAll() {
		ArrayList<Player> returnlist = new ArrayList<Player>();
		returnlist.addAll(players);
		returnlist.addAll(spectators);
		returnlist.addAll(lobby.players);
		return returnlist;
	}
	
	public boolean contains(Player p) {
		return getAll().contains(p);
	}
	
	
}
