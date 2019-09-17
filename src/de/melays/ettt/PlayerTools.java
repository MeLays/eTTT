/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.ettt;

import java.util.ArrayList;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import io.github.theluca98.textapi.Title;

public class PlayerTools {
	
	public static void clearInventory(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}
	
	public static void resetPlayer(Player p) {
		p.closeInventory();
		clearInventory(p);
		p.setFallDistance(0);
		
		AttributeInstance healthAttribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		healthAttribute.setBaseValue(20);
		
		p.setHealth(20);
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
	
	public static void sendTitle (ArrayList<Player> players , String title , String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
		for (Player p : players) {
			new Title(title , subtitle , fadeInTime , showTime , fadeOutTime).send(p);
		}
	}
	
	public static void sendTitle (Player player , String title , String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
		new Title(Main.c(title) , Main.c(subtitle) , fadeInTime , showTime , fadeOutTime).send(player);
	}
	
	public static void sendTitle (Configuration config , String path ,Player p) {
		ConfigurationSection section = config.getConfigurationSection(path);
		new Title(Main.c(section.getString("title")) , Main.c(section.getString("subtitle")) , section.getInt("fadein") , section.getInt("show") , section.getInt("fadeout")).send(p);
	}
	
}
