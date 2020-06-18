package de.melays.ettt.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.melays.ettt.Main;
import de.melays.ettt.PlayerTools;
import de.melays.ettt.game.lobby.Lobby;
import de.melays.ettt.game.lobby.LobbyMode;
import de.melays.ettt.game.tester.ArenaTester;
import de.melays.ettt.log.Logger;
import de.melays.ettt.tools.ColorTabAPI;
import de.melays.ettt.tools.ScoreBoardTools;
import de.melays.ettt.tools.Tools;
import io.github.theluca98.textapi.ActionBar;

public class Arena {
	
	//Main Instance
	Main main;
	
	//Data
	public String name;
	public String display;
	public Material displayitem = Material.PAPER;
	public int min;
	public int max;
	public int started_players = 0;
	public int repeatGame = 1;
	public int repeatGameTotal = 1;
	
	//Lobby
	public Lobby lobby;
	
	//State
	public ArenaState state = ArenaState.LOBBY;
	
	//Players
	public ArrayList<Player> spectators = new ArrayList<Player>();
	HashMap<Player , ScoreBoardTools> scoreboard = new HashMap<Player , ScoreBoardTools>();
	public RoleManager roleManager;
	RolePackage rolePackage;
	public HashMap<Player ,Integer> points = new HashMap<Player ,Integer>();
	
	//Karma at the beginning of the game (to show to other players)
	public HashMap<Player ,Integer> karma_at_start = new HashMap<Player ,Integer>();
	//Current karma to prevent database spam
	public HashMap<Player ,Integer> current_karma = new HashMap<Player ,Integer>();

	
	//Counter
	int counter = 0;
	int warmup_counter;
	int game_counter;
	int end_counter;
	
	//Chestmap
	public HashMap<Location, Inventory> chests = new HashMap<Location, Inventory>();
	
	//Map Reset
	public MapReset mapReset;
	
	//Tester
	public ArenaTester tester;
	
	public Arena (Main main , String name) {
		this.main = main;
		
		//Load the repeation time
		this.repeatGame = main.getConfig().getInt("game.repeat_game");
		this.repeatGameTotal = this.repeatGame;
		
		Logger.log(main.prefix + " [Arena (name="+name+")] Loading...");
		this.name = name;
		
		this.roleManager = new RoleManager (main , this);
		this.tester = new ArenaTester(main, this);
		this.tester.load();
		
		this.display = main.getArenaManager().getConfiguration().getString(name+".display");
		this.min = main.getArenaManager().getConfiguration().getInt(name+".players.min");
		this.max = main.getArenaManager().getConfiguration().getInt(name+".players.max");
		try {
			String itemname = main.getArenaManager().getConfiguration().getString(name+".display_item");
			if (itemname != null) {
				Material m = Material.getMaterial(itemname.toUpperCase());
				if (m == null) {
					m = Material.getMaterial(itemname.toUpperCase(), true);
					if (m != null) {
						this.displayitem = m;
					}
				}
				else {
					this.displayitem = m;
				}
			}
		}catch(Exception e) {
			
		}
		if (Tools.isLocationSet(main.getArenaManager().getConfiguration(), name + ".lobby"))
			this.lobby = new Lobby(main , Tools.getLocation(main.getArenaManager().getConfiguration(), name + ".lobby"));
		else
			this.lobby = new Lobby(main , null);
		this.lobby.setMode(LobbyMode.FIXED);
		this.lobby.setArena(this);
		this.lobby.startLoop();
		
		this.mapReset = new MapReset();
		
		//Load Counters
		warmup_counter = main.getConfig().getInt("game.countdowns.game.warmup");
		game_counter = main.getConfig().getInt("game.countdowns.game.game");
		end_counter = main.getConfig().getInt("game.countdowns.game.end");
		counter = this.warmup_counter;
		
	}
	
	public void stop(boolean load) {
		ArrayList<Player> all = this.getAll();
		this.roleManager.resetTabColors();
		lobby.players = null;
		this.roleManager.traitors = null;
		this.roleManager.detectives = null;
		this.roleManager.innocents = null;
		this.roleManager.none = null;
		this.spectators = null;
		Bukkit.getScheduler().cancelTask(id);
		main.getArenaManager().unregister(this);
		lobby.destroy();
		
		this.mapReset.resetAll();

		//Reload arena again so its ready for the bungeelobby
		
		if (load) {
			main.getArenaManager().load(name);
		}
		
		if (main.isBungeeMode()) {
			try {
				main.resetBungeeLobby();
			}catch(Exception ex) {
				
			}
		}
		
		for (Player p : all) {
			p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.leave").toUpperCase()));
			p.teleport(main.getArenaManager().getGlobalLobby());
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.setFlying(false);
			PlayerTools.resetPlayer(p);
			if (main.isBungeeMode()) {
				main.getBungeeCordLobby().join(p);
			}
		}
		if (main.isBungeeMode()) {
			main.current = null;
		}
	}
	
	public void restartKeepPlayers() {
		
		//Cancle old loop
		Bukkit.getScheduler().cancelTask(id);
		
		//Create and fill a new role manager
		
		@SuppressWarnings("unchecked")
		ArrayList<Player> all = (ArrayList<Player>) this.getAll().clone();
		
		this.roleManager.resetTabColors();
		lobby.players = new ArrayList<Player>();
		this.roleManager.traitors = new ArrayList<Player>();
		this.roleManager.detectives = new ArrayList<Player>();
		this.roleManager.innocents = new ArrayList<Player>();
		this.roleManager.none = new ArrayList<Player>();
		this.spectators = new ArrayList<Player>();
		
		this.roleManager = new RoleManager (main , this);
		
		for (Player p : all) {
			this.roleManager.none.add(p);
		}
		
		//Load Counters
		warmup_counter = main.getConfig().getInt("game.countdowns.game.warmup");
		game_counter = main.getConfig().getInt("game.countdowns.game.game");
		end_counter = main.getConfig().getInt("game.countdowns.game.end");
		counter = this.warmup_counter;
		
		//Set arena state and create new RolePackage
		
		this.state = ArenaState.WARMUP;		
		this.rolePackage = new RolePackage();
		
		//Reset Chest Inventories
		chests = new HashMap<Location, Inventory>();
		
		//Teleport Players to new spawnpoints
		
		ArrayList<Location> spawns = Tools.getLocationsCounting(main.getArenaManager().getConfiguration(), name.toLowerCase()+".spawns");
		Collections.shuffle(spawns);
		int i = 0;
		
		for (Player p : this.getAll()) {
			if (i >= spawns.size()) i = 0;
			p.teleport(spawns.get(i));
			PlayerTools.resetPlayer(p);
			p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.game").toUpperCase()));
			ArenaScoreboard.createPlayerScoreboard(this, p);
			i++;
		}
		
		//Load karma into map
		for (Player p : this.getAllPlaying()) {
			int karma = main.getStatsManager().getDisplayKarma(p);
			karma_at_start.put(p, karma);
			current_karma.put(p, karma);
			
			//Update player Level
			p.setExp(0);
			p.setLevel(karma);
		}
		
		this.mapReset.resetAll();
		
		//Start new loop
		updateAll();
		startLoop();
	}
	
	public void restart() {
		
		
		if (this.roleManager != null && main.addonCorpseReborn) {
			this.roleManager.corpseContainer.removeAll();
		}
		
		this.repeatGame --;
		
		if (this.repeatGame <= 0) {
			stop(true);
		}
		else {
			//Prepeare the arena for another game.
			restartKeepPlayers();
		}
	}
	
	public void broadcast (String msg) {
		for (Player p : this.getAll()) {
			p.sendMessage(Main.c(msg));
		}
	}
	
	public void sendRadiusMessage(Player p , String msg){
		double maxDist = 10;
		for (Player other : Bukkit.getOnlinePlayers()) {
			if (other.getWorld().equals(p.getWorld())) {
				if (other.getLocation().distance(p.getLocation()) <= maxDist) {
					if (getAllPlaying().contains(other) || this.spectators.contains(other)){
						other.sendMessage(msg);
					}
				}
			}
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
		if (main.isBungeeMode() && main.getBungeeCordLobby().contains(p)) return false;
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
		
		//Save Inventory
		main.getInventorySaver().saveInventory(p);
		
		PlayerTools.resetPlayer(p);
		p.setAllowFlight(true);
		p.setFlying(true);
		p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.spectator")));
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
		p.setGameMode(GameMode.valueOf(main.getConfig().getString("gamemodes.spectator")));
		updateAll();
	}
	
	public void checkWin() {
		if (this.roleManager.traitors.size() == 0) {
			for (Player p : this.getAllPlaying()) {
				if (roleManager.getRole(p) == Role.DETECTIVE || roleManager.getRole(p) == Role.INNOCENT) {
					p.sendMessage(main.getMessageFetcher().getMessage("game.end.player.won", true));
					PlayerTools.sendTitle(p, main.getSettingsFile().getConfiguration().getString("game.titles.win.innocent"),
							main.getSettingsFile().getConfiguration().getString("game.titles.win.you_won")
							, 0, 60, 20);
					main.getStatsManager().addWin(p);
				}
				else {
					p.sendMessage(main.getMessageFetcher().getMessage("game.end.player.lost", true));
					PlayerTools.sendTitle(p, main.getSettingsFile().getConfiguration().getString("game.titles.win.innocent"),
							main.getSettingsFile().getConfiguration().getString("game.titles.win.you_lost")
							, 0, 60, 20);
					main.getStatsManager().addLost(p);
				}
				main.getStatsManager().addGame(p);
			}
			String traitors = this.roleManager.listToString(this.roleManager.traitors_beginning , main.getMessageFetcher().getMessage("game.role.traitor_spacer", true));
			for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("game.end.innocent_win")) {
				this.broadcast(Main.c(s).replaceAll("%prefix%", main.prefix).replaceAll("%traitors%", traitors));
			}
			switchToEnd();
		}
		else if (this.roleManager.innocents.size() + this.roleManager.detectives.size() == 0) {
			for (Player p : this.getAllPlaying()) {
				if (roleManager.getRole(p) == Role.TRAITOR) {
					p.sendMessage(main.getMessageFetcher().getMessage("game.end.player.won", true));
					PlayerTools.sendTitle(p, main.getSettingsFile().getConfiguration().getString("game.titles.win.traitor"),
							main.getSettingsFile().getConfiguration().getString("game.titles.win.you_won")
							, 0, 60, 20);
					main.getStatsManager().addWin(p);
				}
				else {
					p.sendMessage(main.getMessageFetcher().getMessage("game.end.player.lost", true));
					PlayerTools.sendTitle(p, main.getSettingsFile().getConfiguration().getString("game.titles.win.traitor"),
							main.getSettingsFile().getConfiguration().getString("game.titles.win.you_lost")
							, 0, 60, 20);
					main.getStatsManager().addLost(p);
				}
				main.getStatsManager().addGame(p);
			}
			String traitors = this.roleManager.listToString(this.roleManager.traitors_beginning , main.getMessageFetcher().getMessage("game.role.traitor_spacer", true));
			for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("game.end.traitor_win")) {
				this.broadcast(Main.c(s).replaceAll("%prefix%", main.prefix).replaceAll("%traitors%", traitors));
			}
			switchToEnd();
		}
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
		main.getStatsManager().addGame(p);
		main.getStatsManager().addLost(p);
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
		//Restore Inventory
		main.getInventorySaver().restoreInventory(p);
		if (state != ArenaState.LOBBY && state != ArenaState.END) {
			if (this.getAllPlaying().size() < 1) {
				restart();
			}
			else {
				checkWin();
			}
		}
		if (state == ArenaState.END && this.getAllPlaying().size() == 0) {
			restart();
		}
		else if (this.state != ArenaState.LOBBY) updateAll();
	}
	
	public void receiveFromLobby(ArrayList<Player> players , RolePackage rolePackage) {
		this.rolePackage = rolePackage;
		this.state = ArenaState.WARMUP;
		ArrayList<Location> spawns = Tools.getLocationsCounting(main.getArenaManager().getConfiguration(), name.toLowerCase()+".spawns");
		Collections.shuffle(spawns);
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
		
		//Load karma into map
		for (Player p : this.getAllPlaying()) {
			int karma = main.getStatsManager().getDisplayKarma(p);
			karma_at_start.put(p, karma);
			current_karma.put(p, karma);
			
			//Update player Level
			p.setExp(0);
			p.setLevel(karma);
		}
		
		updateAll();
		startLoop();
	}
	
	//LOOP
	int id;
	Arena instance = this;
	int updateCounter = 0;
	public void startLoop() {
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {
				
				for (Player p : instance.getAll()) {
					ArenaScoreboard.updateScoreBoard(instance , p);
					if (updateCounter % 5 == 0)
						roleManager.updateTabColors();
					updateCounter ++;
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
						broadcast(main.getMessageFetcher().getMessage("game.end.player.draw", true));
						String traitors = roleManager.listToString(roleManager.traitors_beginning , main.getMessageFetcher().getMessage("game.role.traitor_spacer", true));
						for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("game.end.draw")) {
							broadcast(Main.c(s).replaceAll("%prefix%", main.prefix).replaceAll("%traitors%", traitors));
						}
						switchToEnd();
						return;
					}
					
					if (((counter >= 30 && counter % 15 == 0) || (counter < 30 && counter % 10 == 0) || counter <= 5) && counter <= 90) {
						
						//Check for restart
						if (repeatGame > 0) {
							broadcast(main.getMessageFetcher().getMessage("game.countdown.restart", true).replaceAll("%seconds%", counter + ""));
						}
						else
							broadcast(main.getMessageFetcher().getMessage("game.countdown.end", true).replaceAll("%seconds%", counter + ""));
					}
					
					counter -= 1;
					
					//LEAVE DAMAGE PUNISHMENT
					if (main.getConfig().getBoolean("game.barrier.leave")) {
						Location min = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), name.toLowerCase() + ".arena.min");
						Location max = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), name.toLowerCase() + ".arena.max");
						for (Player p : getAllPlaying()) {
							if (!Tools.isInArea(p.getLocation(), min, max)) {
								if (p.getHealth() > main.getConfig().getInt("game.barrier.leave-damage")) {
									p.damage(main.getConfig().getInt("game.barrier.leave-damage"));
									PlayerTools.sendTitle(main.getSettingsFile().getConfiguration(), "game.titles.warning.play-area", p);
								}
								else
									roleManager.callKill(p);
							}
						}
					}
					
				}
				
				if (instance.state == ArenaState.END) {
					
					if (counter == 0) {
						restart();
					}
					
					else if (((counter >= 30 && counter % 15 == 0) || (counter < 30 && counter % 10 == 0) || counter <= 5) && counter <= 90) {
						//Check for restart
						if (repeatGame > 1) {
							broadcast(main.getMessageFetcher().getMessage("game.countdown.restart", true).replaceAll("%seconds%", counter + ""));
						}
						else
							broadcast(main.getMessageFetcher().getMessage("game.countdown.stop", true).replaceAll("%seconds%", counter + ""));
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
		this.started_players = this.getAllPlaying().size();
		updateAll();
	}
	
	public void switchToEnd() {
		this.state = ArenaState.END;
		this.counter = this.end_counter;
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
		updateVisibility();
	}
	
	public void updateVisibility() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!this.getAll().contains(p)) {
				for (Player p1 : this.getAll()) {
					p.hidePlayer(main ,p1);
					p1.hidePlayer(main ,p);
				}
			}
		}
		for (Player p : this.getAll()) {
			for (Player p1 : this.getAll()) {
				p.showPlayer(main ,p1);
			}
		}
		if (this.state != ArenaState.END)
			for (Player p : this.getAllPlaying()) {
				for (Player p1 : this.spectators) {
					p.hidePlayer(main ,p1);
				}
			}
	}
	
}
