package de.melays.ettt.tools;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Tools {
	
	public static boolean isInArea (Location loc , Location in1 , Location in2) {
		Location[] locs = generateMaxMinPositions(in1 , in2);
		Location min = locs[0];
		Location max = locs[1];
		if (loc.getX() >= min.getX() && loc.getY() >= min.getY() && loc.getZ() >= min.getZ()) {
			if (loc.getX() <= max.getX() && loc.getY() <= max.getY() && loc.getZ() <= max.getZ()) {
				return true;
			}
		}
		return false;
	}
	
	public static void cylinder(Location loc, Material mat, int r) {
	    int cx = loc.getBlockX();
	    int cy = loc.getBlockY();
	    int cz = loc.getBlockZ();
	    World w = loc.getWorld();
	    int rSquared = r * r;
	    for (int x = cx - r; x <= cx +r; x++) {
	        for (int z = cz - r; z <= cz +r; z++) {
	            if ((cx - x) * (cx - x) + (cz - z) * (cz - z) <= rSquared) {
	                w.getBlockAt(x, cy, z).setType(mat);
	            }
	        }
	    }
	}
	
	public static boolean isInAreaIgnoreHeight (Location loc , Location in1 , Location in2) {
		Location[] locs = generateMaxMinPositions(in1 , in2);
		Location min = locs[0];
		Location max = locs[1];
		if (loc.getX() >= min.getX() && loc.getZ() >= min.getZ()) {
			if (loc.getX() <= max.getX() && loc.getZ() <= max.getZ()) {
				return true;
			}
		}
		return false;
	}
	
	public static Location[] generateMaxMinPositions (Location l1 , Location l2){
		double xpos1;
		double ypos1;
		double zpos1;
		double xpos2;
		double ypos2;
		double zpos2;
		if (l1.getX() <= l2.getX()){	
			xpos1 = l1.getX();
			xpos2 = l2.getX();	
		}
		else{	
			xpos1 = l2.getX();
			xpos2 = l1.getX();	
		}
		if (l1.getY() <= l2.getY()){	
			ypos1 = l1.getY();
			ypos2 = l2.getY();	
		}
		else{
			ypos1 = l2.getY();
			ypos2 = l1.getY();
		}
		if (l1.getZ() <= l2.getZ()){
			
			zpos1 = l1.getZ();
			zpos2 = l2.getZ();
		}
		else{	
			zpos1 = l2.getZ();
			zpos2 = l1.getZ();	
		}
		
		Location locs[] = {new Location (l1.getWorld() , xpos1 , ypos1 , zpos1) , new Location (l1.getWorld() , xpos2 , ypos2 , zpos2)};
		return locs;
	}
	
	public static void saveLiteLocation (FileConfiguration config , String path , Location loc) {
		config.set(path + ".x", loc.getBlockX());
		config.set(path + ".y", loc.getBlockY());
		config.set(path + ".z", loc.getBlockZ());
		config.set(path + ".world", loc.getWorld().getName());
	}
	
	public static Location getLiteLocation (FileConfiguration config , String path) {
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		String world = config.getString(path + ".world");
		return new Location(Bukkit.getWorld(world) , x , y , z);
	}
	
	public static void saveLocation (FileConfiguration config , String path , Location loc) {
		config.set(path + ".x", loc.getX());
		config.set(path + ".y", loc.getY());
		config.set(path + ".z", loc.getZ());
		config.set(path + ".yaw", loc.getYaw());
		config.set(path + ".pitch", loc.getPitch());
		config.set(path + ".world", loc.getWorld().getName());
	}
	
	public static Location getLocation (FileConfiguration config , String path) {
		double x = config.getDouble(path + ".x");
		double y = config.getDouble(path + ".y");
		double z = config.getDouble(path + ".z");
		double yaw = config.getDouble(path + ".yaw");
		double pitch = config.getDouble(path + ".pitch");
		String world = config.getString(path + ".world");
		return new Location(Bukkit.getWorld(world) , x , y , z , (long)yaw , (long)pitch);
	}
	
	public static boolean isLocationSet (FileConfiguration config , String path) {
		return (config.getString(path + ".world") != null);
	}
	
	public static int addCounting (FileConfiguration config , String counterpath , Location loc) {
		try {
			ConfigurationSection section = config.getConfigurationSection(counterpath);
			Set<String> keys = section.getKeys(false);
			int highest = 0;
			for (String s : keys) {
				try {
					int current = Integer.parseInt(s);
					if (current > highest) {
						highest = current;
					}
				}catch(Exception ex){
					
				}
			}
			int new_loc = highest + 1;
			Tools.saveLocation(config, counterpath+"." + new_loc, loc);
			return new_loc;
		} catch (Exception e) {
			Tools.saveLocation(config, counterpath+"." + 1, loc);
			return 1; 
			
		}
	}
	
	public static void setLocationCounting (FileConfiguration config , String path , int id , Location loc) {
		Tools.saveLocation(config, path+".id", loc);
	}
	
	public static void removeLocationCounting (FileConfiguration config , String counterpath , int id) {
		config.set(counterpath+"."+id, null);
	}
	
	public static ArrayList<Location> getLocationsCounting (FileConfiguration config , String counterpath) {
		ConfigurationSection section = config.getConfigurationSection(counterpath);
		ArrayList<Location> locs = new ArrayList<Location>();
		try {
			Set<String> keys = section.getKeys(false);
			for (String s : keys) {
				locs.add(Tools.getLocation(config, counterpath+"."+s));
			}
			return locs;
		} catch (Exception e) {
			return locs;
		}
	}
	
}
