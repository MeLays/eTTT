package de.melays.ettt.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.entity.Player;

import de.melays.ettt.Main;

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
		
		for (Player p : all) {
			if (rolePackage.getRole(p.getUniqueId()) != null) {
				if (rolePackage.getRole(p.getUniqueId()) == Role.TRAITOR && !(traitors >= 1)) {
					setRole(p , rolePackage.getRole(p.getUniqueId()));
					traitors -= 1;
					continue;
				}
				else if (rolePackage.getRole(p.getUniqueId()) == Role.DETECTIVE && !(detectives >= 1)) {
					setRole(p , rolePackage.getRole(p.getUniqueId()));
					detectives -= 1;
					continue;
				}
				setRole(p , Role.INNOCENT);
			}
		}
	}
	
	public void setRole(Player p, Role role) {
		if (role == Role.INNOCENT) {
			innocents.add(p);
		}
		else if (role == Role.TRAITOR) {
			traitors.add(p);
		}
		else if (role == Role.DETECTIVE) {
			detectives.add(p);
		}
	}
	
}
