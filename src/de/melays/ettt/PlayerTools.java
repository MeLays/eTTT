/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class PlayerTools {
	
	public static void clearInventory(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}
	
	public static void resetPlayer(Player p) {
		p.closeInventory();
		clearInventory(p);
		p.setFallDistance(0);
		p.setMaxHealth(20);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(40);
		p.setLevel(0);
		p.setExp(0);
		p.setVelocity(new Vector());
	    for (PotionEffect effect : p.getActivePotionEffects())
	        p.removePotionEffect(effect.getType());
	}
	
	public static void setLevel (ArrayList<Player> players , int lvl) {
		for (Player p : players) {
			p.setExp(0);
			p.setLevel(lvl);
		}
	}
	
}
