package de.melays.ettt.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.PlayerTools;
import de.melays.ettt.tools.ColorTabAPI;

public class RoleManager {
	
	Main main;
	Arena arena;
	
	public RoleManager (Main main , Arena arena) {
		this.main = main;
		this.arena = arena;
	}
	
	ArrayList<Player> traitors = new ArrayList<Player>();
	ArrayList<Player> detectives = new ArrayList<Player>();
	ArrayList<Player> innocents = new ArrayList<Player>();
	ArrayList<Player> none = new ArrayList<Player>();
	
	ArrayList<String> traitors_beginning = new ArrayList<String>();
	ArrayList<String> detectives_beginning = new ArrayList<String>();

	public void giveRoles(RolePackage rolePackage) {
		
		ArrayList<Player> all = arena.getAllPlaying();
		Collections.shuffle(all , new Random(System.currentTimeMillis()));
		
		int traitors = (main.getConfig().getInt("game.ratio.traitor") / 100) * arena.getAllPlaying().size();
		int detectives = (main.getConfig().getInt("game.ratio.detective") / 100) * arena.getAllPlaying().size();
		
		if (traitors == 0) traitors = 1;
		if (traitors + detectives > arena.getAllPlaying().size()) {
			for (int i = 0 ; i < (traitors + detectives - arena.getAllPlaying().size()) ; i++) {
				if (detectives != 0) detectives -= 1;
				else {
					traitors -= 1;
				}
			}
		}
		if (traitors == 0 || arena.getAll().size() == 1) arena.restart();
		
		for (Player p : new ArrayList<Player>(all)) {
			if (rolePackage.getRole(p.getUniqueId()) != null) {
				if (rolePackage.getRole(p.getUniqueId()) == Role.TRAITOR && !(traitors >= 1)) {
					setRole(p , rolePackage.getRole(p.getUniqueId()));
					traitors -= 1;
					all.remove(p);
					continue;
				}
				else if (rolePackage.getRole(p.getUniqueId()) == Role.DETECTIVE && !(detectives >= 1)) {
					setRole(p , rolePackage.getRole(p.getUniqueId()));
					detectives -= 1;
					all.remove(p);
					continue;
				}
			}
		}
		for (Player p : new ArrayList<Player>(all)) {
			if (traitors >= 1) {
				setRole(p , Role.TRAITOR);
				traitors -= 1;
			}
			else if (detectives >= 1) {
				setRole(p , Role.DETECTIVE);
				detectives -= 1;
			}
			else {
				setRole(p , Role.INNOCENT);
			}
		}
		none = new ArrayList<Player>();
		for (Player p : this.traitors) {
			traitors_beginning.add(p.getName());
		}
		for (Player p : this.detectives) {
			this.detectives_beginning.add(p.getName());
		}
	}
	
	public void setRole(Player p, Role role) {
		if (role == Role.INNOCENT) {
			innocents.add(p);
			PlayerTools.sendTitle(main.getSettingsFile().getConfiguration(), "game.titles.roles.innocent", p);
		}
		else if (role == Role.TRAITOR) {
			traitors.add(p);
			PlayerTools.sendTitle(main.getSettingsFile().getConfiguration(), "game.titles.roles.traitor", p);
		}
		else if (role == Role.DETECTIVE) {
			detectives.add(p);
			PlayerTools.sendTitle(main.getSettingsFile().getConfiguration(), "game.titles.roles.detective", p);
		}
	}
	
	public Role getRole(Player p) {
		if (this.traitors.contains(p)) return Role.TRAITOR;
		if (this.detectives.contains(p)) return Role.DETECTIVE;
		if (this.innocents.contains(p)) return Role.INNOCENT;
		return null;
	}
	
	public String roleToDisplayname (Role role) {
		if (role == null) return Main.c(main.getSettingsFile().getConfiguration().getString("roles.none"));
		if (role == Role.TRAITOR) return Main.c(main.getSettingsFile().getConfiguration().getString("roles.traitor.display"));
		if (role == Role.DETECTIVE) return Main.c(main.getSettingsFile().getConfiguration().getString("roles.detective.display"));
		if (role == Role.INNOCENT) return Main.c(main.getSettingsFile().getConfiguration().getString("roles.innocent.display"));
		return Main.c(main.getSettingsFile().getConfiguration().getString("roles.none"));
	}
	
	public String listToString (ArrayList<String> list , String sepperator) {
		String r = "";
		for (String s : list) {
			if (list.indexOf(s) != list.size() - 1)
				r += s + sepperator;
			else
				r += s;
		}
		return Main.c(r);
	}
	
	public void sendRoleMessages () {
		String detectives = listToString(this.detectives_beginning , main.getMessageFetcher().getMessage("game.role.detective_spacer", true));
		String traitors = listToString(this.traitors_beginning , main.getMessageFetcher().getMessage("game.role.traitor_spacer", true));
		for (Player p : arena.getAllPlaying()) {
			if (getRole(p) == Role.INNOCENT) {
				for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("game.role.innocent")) {
					if (s.contains("%detectives%") && this.detectives.size() == 0) continue;
					p.sendMessage(Main.c(s).replaceAll("%prefix%", main.prefix).replaceAll("%detectives%", detectives));
				}
			}
			else if (getRole(p) == Role.DETECTIVE) {
				for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("game.role.detective")) {
					if (s.contains("%detectives%") && this.detectives.size() == 0) continue;
					p.sendMessage(Main.c(s).replaceAll("%prefix%", main.prefix).replaceAll("%detectives%", detectives));
				}
			}
			else if (getRole(p) == Role.TRAITOR) {
				for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("game.role.traitor")) {
					if (s.contains("%detectives%") && this.detectives.size() == 0) continue;
					p.sendMessage(Main.c(s).replaceAll("%prefix%", main.prefix).replaceAll("%detectives%", detectives).replaceAll("%traitors%", traitors));
				}
			}
		}
	}
	
	public void resetTabColors () {
		for (Player p1 : arena.getAll()) {
			ColorTabAPI.clearTabStyle(p1, arena.getAll());
		}
	}
	
	public void updateTabColors() {
		resetTabColors();
		//TRAITORS
		for (Player p1 : arena.getAllPlaying()) {
			if (getRole(p1) == null) {
				ColorTabAPI.setTabStyle(p1, main.getSettingsFile().getConfiguration().getString("game.tab.none.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.none.suffix"), 9, this.traitors);
			}
			else if (getRole(p1) == Role.TRAITOR) {
				ColorTabAPI.setTabStyle(p1, main.getSettingsFile().getConfiguration().getString("game.tab.traitor.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.traitor.suffix"), 0, this.traitors);
			}
			else if (getRole(p1) == Role.DETECTIVE) {
				ColorTabAPI.setTabStyle(p1, main.getSettingsFile().getConfiguration().getString("game.tab.detective.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.detective.suffix"), 1, this.traitors);
			}
			else if (getRole(p1) == Role.INNOCENT) {
				ColorTabAPI.setTabStyle(p1, main.getSettingsFile().getConfiguration().getString("game.tab.innocent_traitor_view.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.innocent_traitor_view.suffix"), 2, this.traitors);
			}
		}
		//INNOCENTS
		ArrayList<Player> receivers = new ArrayList<Player>();
		receivers.addAll(this.detectives);
		receivers.addAll(this.innocents);
		receivers.addAll(this.none);
		receivers.addAll(arena.spectators);
		for (Player p1 : arena.getAllPlaying()) {
			if (getRole(p1) == null) {
				ColorTabAPI.setTabStyle(p1, main.getSettingsFile().getConfiguration().getString("game.tab.none.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.none.suffix"), 9, receivers);
			}
			else if (getRole(p1) == Role.DETECTIVE) {
				ColorTabAPI.setTabStyle(p1, main.getSettingsFile().getConfiguration().getString("game.tab.detective.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.detective.suffix"), 1, receivers);
			}
			else if (getRole(p1) == Role.INNOCENT || getRole(p1) == Role.TRAITOR) {
				ColorTabAPI.setTabStyle(p1, main.getSettingsFile().getConfiguration().getString("game.tab.innocent.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.innocent.suffix"), 2, receivers);
			}
		}
		//SPECTATORS
		for (Player p1 : arena.spectators) {
			ColorTabAPI.setTabStyle(p1,  main.getSettingsFile().getConfiguration().getString("game.tab.spectators.prefix"), main.getSettingsFile().getConfiguration().getString("game.tab.spectators.suffix"), 9, arena.getAll());
		}
	}
	
}
