package de.melays.ettt.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
	public String display;
	public int min;
	public int max;
	
	//Lobby
	public Lobby lobby;
	
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
		this.min = main.getArenaManager().getConfiguration().getInt(name+".players.min");
		this.max = main.getArenaManager().getConfiguration().getInt(name+".players.max");
		this.lobby = new Lobby(main , Tools.getLocation(main.getArenaManager().getConfiguration(), name + ".lobby"));
		this.lobby.setMode(LobbyMode.FIXED);
		this.lobby.setArena(this);
		this.lobby.startLoop();
	}
	
	public void stop() {
		ArrayList<Player> all = this.getAll();
		lobby.players = null;
		this.players = null;
		this.spectators = null;
		main.getArenaManager().unregister(this);
		for (Player p : all) {
			p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.leave").toUpperCase()));
			p.teleport(main.getArenaManager().getGlobalLobby());
			PlayerTools.resetPlayer(p);
		}
	}
	
	public void restart() {
		stop();
		main.getArenaManager().load(name);
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
		if (this.getAllPlaying().size() >= max) return false;
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
	
	public void checkWin() {
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
		if (state != ArenaState.LOBBY) {
			if (this.getAllPlaying().size() <= 1) {
				restart();
			}
			else {
				checkWin();
			}
		}
		if (state == ArenaState.END && this.getAllPlaying().size() == 0) {
			restart();
		}
	}
	
	public void receiveFromLobby(ArrayList<Player> players , RolePackage rolePackage) {
		this.players.addAll(players);
		this.state = ArenaState.WARMUP;
		ArrayList<Location> spawns = Tools.getLocationsCounting(main.getArenaManager().getConfiguration(), name.toLowerCase()+".spawns");
		int i = 0;
		for (Player p : players) {
			if (i >= spawns.size()) i = 0;
			p.teleport(spawns.get(i));
			PlayerTools.resetPlayer(p);
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.game").toUpperCase()));
			i++;
		}
	}
	
}
