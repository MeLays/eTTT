package de.melays.ettt.log;

import org.bukkit.Bukkit;

import de.melays.ettt.Main;

public class Logger {
	
	public static void log (String msg) {
		Bukkit.getConsoleSender().sendMessage(Main.c(msg));
	}
	
}
