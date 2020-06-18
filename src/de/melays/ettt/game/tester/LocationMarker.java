package de.melays.ettt.game.tester;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationMarker {
	
	Location locl;
	Location locr;
	Player p;
	
	public LocationMarker(Player ps){
		p = ps;
	}
	
	public void setLeft (Location l){
		locl = l;
	}
	
	public void setRight (Location l){
		locr = l;
	}
	
	public boolean complete (){
		if (locr != null && locl != null){
			return true;
		}
		return false;
	}
}
