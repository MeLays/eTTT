package de.melays.ettt.game;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.PlayerTools;
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
	public ArenaState state = ArenaState.LOBBY;
	
	//Players
	ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Player> spectators = new ArrayList<Player>();
	
	public Arena (Main main , String name) {
		this.main = main;
		
		Logger.log(main.prefix + " [Arena (name="+name+")] Loading...");
		
		this.name = name;
		this.display = main.getArenaManager().getConfiguration().getString(name+".display");
		this.lobby = new Lobby(main , Tools.getLocation(main.getArenaManager().getConfiguration(), name + ".lobby"));
		this.lobby.setMode(LobbyMode.FIXED);
		this.lobby.setArena(this);
	}
	
	public void stop() {
		ArrayList<Player> all = this.getAll();
		lobby.players = null;
		this.players = null;
		this.spectators = null;
		for (Player p : all) {
			p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.leave").toUpperCase()));
			p.teleport(main.getArenaManager().getGlobalLobby());
			PlayerTools.resetPlayer(p);
		}
	}
	
	public void broadcast (String msg) {
		for (Player p : this.getAll()) {
			p.sendMessage(Main.c(msg));
		}
	}
	
	public ArrayList<Player> getAll() {
		ArrayList<Player> returnlist = new ArrayList<Player>();
		returnlist.addAll(players);
		returnlist.addAll(spectators);
		returnlist.addAll(lobby.players);
		return returnlist;
	}
	
	public ArrayList<Player> getAllPlaying() {
		ArrayList<Player> returnlist = new ArrayList<Player>();
		returnlist.addAll(players);
		return returnlist;
	}
	
	public boolean contains(Player p) {
		return getAll().contains(p);
	}
	
	//Player methods
	public boolean join (Player p) {
		if (main.getArenaManager().isInGame(p)) return false;
		if (state == ArenaState.LOBBY) {
			lobby.join(p);
		}
		else {
			addSpectator(p);
		}
		return true;
	}
	
	public void addSpectator(Player p) {
		//TODO
	}
	
	public void leave(Player p) {
		if (state == ArenaState.LOBBY) {
			lobby.broadcast(main.getMessageFetcher().getMessage("game.leave", true).replaceAll("%player%", p.getName()));
			lobby.remove(p);
		}
		else {
			broadcast(main.getMessageFetcher().getMessage("game.leave-ingame", true)
					.replaceAll("%player%", p.getName())
					.replaceAll("%remaining%", this.getAllPlaying().size()-1 + ""));
		}
		
		if (this.players.contains(p)) this.players.remove(p);
		if (this.spectators.contains(p)) this.spectators.remove(p);
		
		PlayerTools.resetPlayer(p);
		p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.leave").toUpperCase()));
		p.teleport(main.getArenaManager().getGlobalLobby());
	}
	
	
}
