package de.melays.ettt.game;

import java.util.List;

import org.bukkit.entity.Player;

import de.melays.ettt.Main;
import de.melays.ettt.tools.ScoreBoardTools;

public class ArenaScoreboard {
	
	public static void createPlayerScoreboard(Arena arena , Player p) {
		ScoreBoardTools tools = new ScoreBoardTools(p , Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.playing.title")));
		List<String> lines = arena.main.getSettingsFile().getConfiguration().getStringList("game.scoreboard.game.playing.content");
		int value = lines.size();
		for (String s : lines) {
			if (s.equals("time-line")) {
				String divider = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.playing.timer.divider"));
				String minutes = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.playing.timer.minutes"));
				String seconds = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.playing.timer.seconds"));
				int sec = (arena.counter) % 60;
				int min = ((arena.counter) - sec) / 60;
				minutes = minutes.replaceAll("%minutes%", String.format("%02d", min) + "");
				seconds = seconds.replaceAll("%seconds%", String.format("%02d", sec) + "");
				tools.addLine("timer", minutes, divider , seconds, value);
			}
			else {
				tools.addNormalLine(s
						.replaceAll("%arena%", arena.display)
						.replaceAll("%specs%", arena.spectators.size() + "")
						.replaceAll("%alive%", arena.getAllPlaying().size() + "")
						.replaceAll("%role%", arena.roleManager.roleToDisplayname(arena.roleManager.getRole(p))), value);
			}
			value -= 1;
		}
		tools.set();
		arena.scoreboard.put(p, tools);
	}
	
	public static void createSpectatorScoreboard(Arena arena , Player p) {
		ScoreBoardTools tools = new ScoreBoardTools(p , Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.spectating.title")));
		List<String> lines = arena.main.getSettingsFile().getConfiguration().getStringList("game.scoreboard.game.playing.content");
		int value = lines.size();
		for (String s : lines) {
			if (s.equals("time-line")) {
				String divider = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.spectating.timer.divider"));
				String minutes = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.spectating.timer.minutes"));
				String seconds = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.game.spectating.timer.seconds"));
				int sec = (arena.counter) % 60;
				int min = ((arena.counter) - sec) / 60;
				minutes = minutes.replaceAll("%minutes%", String.format("%02d", min) + "");
				seconds = seconds.replaceAll("%seconds%", String.format("%02d", sec) + "");
				tools.addLine("timer", minutes, divider , seconds, value);
			}
			else {
				tools.addNormalLine(s
						.replaceAll("%arena%", arena.display)
						.replaceAll("%specs%", arena.spectators.size() + "")
						.replaceAll("%alive%", arena.getAllPlaying().size() + "")
						.replaceAll("%role%", Main.c(arena.main.getSettingsFile().getConfiguration().getString("roles.spectator"))), value);
			}
			value -= 1;
		}
		tools.set();
		arena.scoreboard.put(p, tools);
	}

	
	public static void updateScoreBoard(Arena arena , Player p) {
		String minutes = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.lobby.timer.minutes"));
		String seconds = Main.c(arena.main.getSettingsFile().getConfiguration().getString("game.scoreboard.lobby.timer.seconds"));
		int sec = (arena.counter) % 60;
		int min = ((arena.counter) - sec) / 60;
		minutes = minutes.replaceAll("%minutes%", String.format("%02d", min) + "");
		seconds = seconds.replaceAll("%seconds%", String.format("%02d", sec) + "");
		arena.scoreboard.get(p).editPrefix("timer", minutes);
		arena.scoreboard.get(p).editSuffix("timer", seconds);
	}
	
	
}
