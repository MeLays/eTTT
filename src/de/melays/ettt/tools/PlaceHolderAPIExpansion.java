package de.melays.ettt.tools;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolderAPIExpansion extends PlaceholderExpansion{

	Main main;
	
	public PlaceHolderAPIExpansion (Main main) {
		this.main = main;
	}
	
    @Override
    public boolean canRegister(){
        return true;
    }
	
	@Override
	public String getAuthor() {
		return "m-3.me (Schwalboss @ spigotmc.org)";
	}

	@Override
	public String getIdentifier() {
		return "ettt";
	}

	@Override
	public String getVersion() {
		return main.getDescription().getVersion();
	}
	
    @Override
    public String onRequest(OfflinePlayer op, String identifier){
    	if (op == null) {
            return "";
        }
    	if (!op.isOnline()) {
    		return "";
    	}
    	Player p = op.getPlayer();
        if (identifier.equals("role_colored")) {
        	Arena a = main.getArenaManager().searchPlayer(p);
        	if (a == null){
        		return null;
        	}
        	return a.roleManager.roleToDisplayname(a.roleManager.getRole(p));
        }
        if (identifier.equals("role")) {
        	Arena a = main.getArenaManager().searchPlayer(p);
        	if (a == null){
        		return null;
        	}
        	return a.roleManager.getRole(p).toString();
        }
        if (identifier.equals("arena")) {
        	Arena a = main.getArenaManager().searchPlayer(p);
        	if (a == null){
        		return null;
        	}
        	return a.name;
        }
        if (identifier.equals("arena_display")) {
        	Arena a = main.getArenaManager().searchPlayer(p);
        	if (a == null){
        		return null;
        	}
        	return a.display;
        }
        if (identifier.equals("karma")) {
            return main.getStatsManager().getKarma(p) + "";
        }
        if (identifier.equals("passes")) {
            return main.getStatsManager().getPasses(p) + "";
        }
        if (identifier.equals("wins")) {
            return main.getStatsManager().getKey(p.getUniqueId(), "wins") + "";
        }
        if (identifier.equals("lost")) {
            return main.getStatsManager().getKey(p.getUniqueId(), "lost") + "";
        }
        if (identifier.equals("games")) {
            return main.getStatsManager().getKey(p.getUniqueId(), "games") + "";
        }
        return null;
    }

}
