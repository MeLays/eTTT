package de.melays.ettt.game;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.PlayerTools;
import de.melays.ettt.game.lobby.Lobby;
import de.melays.ettt.game.lobby.LobbyMode;
import de.melays.ettt.log.Logger;
import de.melays.ettt.tools.ColorTabAPI;
import de.melays.ettt.tools.ScoreBoardTools;
import de.melays.ettt.tools.Tools;
import io.github.theluca98.textapi.ActionBar;

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
	public ArrayList<Player> spectators = new ArrayList<Player>();
	HashMap<Player , ScoreBoardTools> scoreboard = new HashMap<Player , ScoreBoardTools>();
	RoleManager roleManager;
	RolePackage rolePackage;
	
	//Counter
	int counter = 0;
	int warmup_counter;
	int game_counter;
	int end_counter;
	
	public Arena (Main main , String name) {
		this.main = main;
		
		Logger.log(main.prefix + " [Arena (name="+name+")] Loading...");
		
		this.roleManager = new RoleManager (main , this);
		
		this.name = name;
		this.display = main.getArenaManager().getConfiguration().getString(name+".display");
		this.min = main.getArenaManager().getConfiguration().getInt(name+".players.min");
		this.max = main.getArenaManager().getConfiguration().getInt(name+".players.max");
		this.lobby = new Lobby(main , Tools.getLocation(main.getArenaManager().getConfiguration(), name + ".lobby"));
		this.lobby.setMode(LobbyMode.FIXED);
		this.lobby.setArena(this);
		this.lobby.startLoop();
		
		//Load Counters
		warmup_counter = main.getConfig().getInt("game.countdowns.game.warmup");
		game_counter = main.getConfig().getInt("game.countdowns.game.game");
		end_counter = main.getConfig().getInt("game.countdowns.game.end");
		counter = this.warmup_counter;
		
	}
	
	public void stop() {
		ArrayList<Player> all = this.getAll();
		lobby.players = null;
		this.roleManager.traitors = null;
		this.roleManager.detectives = null;
		this.roleManager.innocents = null;
		this.roleManager.none = null;
		this.spectators = null;
		Bukkit.getScheduler().cancelTask(id);
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
		returnlist.addAll(spectators);
		returnlist.addAll(lobby.players);
		returnlist.addAll(this.roleManager.traitors);
		returnlist.addAll(this.roleManager.detectives);
		returnlist.addAll(this.roleManager.innocents);
		returnlist.addAll(this.roleManager.none);
		return returnlist;
	}
	
	public ArrayList<Player> getAllPlaying() {
		ArrayList<Player> returnlist = new ArrayList<Player>();
		returnlist.addAll(this.roleManager.traitors);
		returnlist.addAll(this.roleManager.detectives);
		returnlist.addAll(this.roleManager.innocents);
		returnlist.addAll(this.roleManager.none);
		return returnlist;
	}
	
	public boolean contains(Player p) {
		return getAll().contains(p);
	}
	
	//Player methods
	public boolean join (Player p) {
		if (main.getArenaManager().isInGame(p)) return false;
		if (this.getAllPlaying().size()+1 > max) return false;
		if (state == ArenaState.LOBBY) {
			lobby.join(p);
		}
		else {
			addSpectator(p);
		}
		return true;
	}
	
	public void addSpectator(Player p) {
		this.spectators.add(p);
		p.teleport(Tools.getLocation(main.getArenaManager().getConfiguration(), name.toLowerCase()+".spectator"));
		PlayerTools.resetPlayer(p);
		p.setAllowFlight(true);
		p.setFlying(true);
		updateAll();
	}
	
	public void moveToSpectator(Player p) {
		if (this.roleManager.traitors.contains(p)) this.roleManager.traitors.remove(p);
		if (this.roleManager.detectives.contains(p)) this.roleManager.detectives.remove(p);
		if (this.roleManager.innocents.contains(p)) this.roleManager.innocents.remove(p);
		if (this.roleManager.none.contains(p)) this.roleManager.none.remove(p);
		if (this.spectators.contains(p)) this.spectators.remove(p);
		this.spectators.add(p);
		p.teleport(Tools.getLocation(main.getArenaManager().getConfiguration(), name.toLowerCase()+".spectator"));
		PlayerTools.resetPlayer(p);
		p.setAllowFlight(true);
		p.setFlying(true);
		updateAll();
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
		if (this.roleManager.traitors.contains(p)) this.roleManager.traitors.remove(p);
		if (this.roleManager.detectives.contains(p)) this.roleManager.detectives.remove(p);
		if (this.roleManager.innocents.contains(p)) this.roleManager.innocents.remove(p);
		if (this.roleManager.none.contains(p)) this.roleManager.none.remove(p);
		if (this.spectators.contains(p)) this.spectators.remove(p);
		PlayerTools.resetPlayer(p);
		ColorTabAPI.clearTabStyle(p, this.getAll());
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.leave").toUpperCase()));
		p.teleport(main.getArenaManager().getGlobalLobby());
		if (state != ArenaState.LOBBY && state != ArenaState.END) {
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
		if (this.state != ArenaState.LOBBY) updateAll();
	}
	
	public void receiveFromLobby(ArrayList<Player> players , RolePackage rolePackage) {
		this.rolePackage = rolePackage;
		this.state = ArenaState.WARMUP;
		ArrayList<Location> spawns = Tools.getLocationsCounting(main.getArenaManager().getConfiguration(), name.toLowerCase()+".spawns");
		int i = 0;
		for (Player p : players) {
			if (i >= spawns.size()) i = 0;
			p.teleport(spawns.get(i));
			PlayerTools.resetPlayer(p);
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.game").toUpperCase()));
			ArenaScoreboard.createPlayerScoreboard(this, p);
			i++;
		}
		this.roleManager.none.addAll(players);
		updateAll();
		startLoop();
	}
	
	//LOOP
	int id;
	Arena instance = this;
	public void startLoop() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {
				
				for (Player p : instance.getAll()) {
					ArenaScoreboard.updateScoreBoard(instance , p);
					if (instance.state == ArenaState.GAME)
						new ActionBar(Main.c(main.getSettingsFile().getConfiguration().getString("game.actionbar.game").replaceAll("%role%", roleManager.roleToDisplayname(roleManager.getRole(p))))).send(p);
				}
				
				if (instance.state == ArenaState.WARMUP) {
					
					if (counter == 0) {
						switchToGame();
						return;
					}
					
					if ((counter >= 30 && counter % 15 == 0) || (counter < 30 && counter % 10 == 0) || counter <= 5) {
						broadcast(main.getMessageFetcher().getMessage("game.countdown.warmup", true).replaceAll("%seconds%", counter + ""));
					}
					
					counter -= 1;
				}
				
				if (instance.state == ArenaState.GAME) {
					
					if (counter == 0) {
						//TODO
						return;
					}
					
					if (((counter >= 30 && counter % 15 == 0) || (counter < 30 && counter % 10 == 0) || counter <= 5) && counter <= 90) {
						broadcast(main.getMessageFetcher().getMessage("game.countdown.end", true).replaceAll("%seconds%", counter + ""));
					}
					
					counter -= 1;
				}
			}
			
		}, 0, 20);
	}
	
	public void switchToGame() {
		this.state = ArenaState.GAME;
		this.roleManager.giveRoles(this.rolePackage);
		this.roleManager.sendRoleMessages();
		this.counter = this.game_counter;
		updateAll();
	}
	
	public void updateAll() {
		for (Player p : instance.getAll()) {
			if (this.spectators.contains(p)) {
				ArenaScoreboard.createSpectatorScoreboard(this, p);
			}
			else {
				ArenaScoreboard.createPlayerScoreboard(this, p);
			}
		}
		roleManager.updateTabColors();
	}
	
	public void updateVisibility() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!this.getAll().contains(p)) {
				for (Player p1 : this.getAll()) {
					p.hidePlayer(p1);
					p1.hidePlayer(p);
				}
			}
		}
		for (Player p : this.getAll()) {
			for (Player p1 : this.getAll()) {
				p.showPlayer(p1);
			}
		}
		for (Player p : this.getAllPlaying()) {
			for (Player p1 : this.spectators) {
				p.hidePlayer(p1);
			}
		}
	}
	
}
