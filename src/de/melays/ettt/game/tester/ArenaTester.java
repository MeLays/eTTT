package de.melays.ettt.game.tester;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import de.melays.ettt.game.ArenaState;
import de.melays.ettt.game.Role;
import de.melays.ettt.log.Logger;
import de.melays.ettt.tools.Tools;

public class ArenaTester {
	
	Main main;
	Arena a;
	
	public ArenaTester (Main m , Arena a){
		main = m;
		this.a = a;
	}
	
	public boolean enabled = false;
	
	public ArrayList<Location> buttons = new ArrayList<Location>();
	ArrayList<Location> diamondblocks = new ArrayList<Location>();
	ArrayList<Location> lamps = new ArrayList<Location>();
	
	public boolean isButton(Location l){
		for (Location loc : buttons){
			if (loc.getBlock().getLocation().equals(l.getBlock().getLocation())){
				return true;
			}
		}
		return false;
	}
	
	public void enableLamps(){
		for (Location loc : lamps){
			loc.getBlock().setType(Material.GLOWSTONE);
		}
	}
	
	public void disableLamps(){
		for (Location loc : lamps){
			loc.getBlock().setType(Material.REDSTONE_LAMP);
		}
	}
	
	public void enableRedstone(){
		for (Location loc : diamondblocks){
			loc.getBlock().setType(Material.REDSTONE_BLOCK);
		}
	}
	
	public void disableRedstone(){
		for (Location loc : diamondblocks){
			loc.getBlock().setType(Material.DIAMOND_BLOCK);
		}
	}
	
	public boolean inTester(Location loc){
		if (smaller.getX() <= loc.getX() && smaller.getY() <= loc.getY() && smaller.getZ() <= loc.getZ()){
			if (bigger.getX() >= loc.getX() && bigger.getY() >= loc.getY() && bigger.getZ() >= loc.getZ()){
				return true;
			}
		}
		return false;
	}
	
	Location bigger;
	Location smaller;
	
	Location outer;
	Location inner;
	
	public void load(){
		Set<String> blocks;
		try{
			blocks = main.getArenaManager().getConfiguration().getConfigurationSection(a.name + ".tester_data.blocks").getKeys(false);
			
			for (String s : blocks){
				
				Location loc = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), a.name + ".tester_data.blocks."+s);
				
				if (loc.getBlock().getType().equals(Material.STONE_BUTTON)){
					buttons.add(loc.getBlock().getLocation());
				}
				else if (loc.getBlock().getType().equals(Material.REDSTONE_LAMP) || loc.getBlock().getType().equals(Material.GLOWSTONE)){
					lamps.add(loc.getBlock().getLocation());
				}
				else if (loc.getBlock().getType().equals(Material.DIAMOND_BLOCK) || loc.getBlock().getType().equals(Material.REDSTONE_BLOCK)){
					diamondblocks.add(loc.getBlock().getLocation());
				}
			}
			
			try {
				smaller = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), a.name + ".tester_data.corner_small");
				bigger = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), a.name + ".tester_data.corner_big");
			}
			catch(IllegalArgumentException e) {
				smaller = null;
				bigger = null;
			}
			
			try {
				outer = Tools.getLocation(main.getArenaManager().getConfiguration(),a.name + ".tester.outer");
				inner = Tools.getLocation(main.getArenaManager().getConfiguration(),a.name + ".tester.inner");
			}
			catch(IllegalArgumentException e) {
				outer = null;
				inner = null;
			}
			
			if (bigger == null || smaller == null || outer == null || inner == null){
				enabled = false;
				
				Logger.log(main.prefix + " Tester of " + a.name + " not loaded:");
				if (bigger == null) Logger.log(main.prefix + "   Tester area not defined!");
				if (smaller == null) Logger.log(main.prefix + "   Tester area not defined!");
				if (outer == null) Logger.log(main.prefix + "   Tester outer location not defined! /tttsetup tester setouter <arena>");
				if (inner == null) Logger.log(main.prefix + "   Tester inner location not defined! /tttsetup tester setinner <arena>");
				
				return;
			}
			
			enabled = true;
		}catch(Exception ex){
			ex.printStackTrace();
			enabled = false;
		}
	}
	
	Player testing = null;
	
	public boolean destroyed = false;
	
	public void testPlayer (Player p){
		if (a.state != ArenaState.GAME){
			p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
			return;
		}
		if (testing != null){
			p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
			return;
		}
		if (a.started_players < main.getConfig().getInt("game.tester.min")){
			p.sendMessage(main.getMessageFetcher().getMessage("game.tester.disabled", true));
			p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
			return;
		}
		p.teleport(inner);
		for (Player pt : a.getAllPlaying()){
			if (pt != p){
				if (this.inTester(pt.getLocation())){
					pt.teleport(this.outer);
				}
			}
		}
		a.sendRadiusMessage(p, main.getMessageFetcher().getMessage("game.tester.entered", true).replace("%player%", p.getName()));
		this.enableRedstone();
		testing = p;
		
		main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			public void run() {
				testing = null;
				disableRedstone();
				if (a.getAllPlaying().contains(p)){
					if (a.roleManager.getRole(p) == Role.TRAITOR){
						enableLamps();
						main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
							public void run() {
								disableLamps();
							}
						}, 50L);
					}
				}
			}
			
		}, 100L);
	}
	
}