package de.melays.ettt.game.lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ettt.Main;
import de.melays.ettt.game.Arena;

public class VotingManager {
	
	Lobby lobby;
	Main main;
	
	Arena vote1 = null;
	Arena vote2 = null;
	Arena vote3 = null;
	
	HashMap<Player , Integer> voted = new HashMap<Player , Integer>();
	
	public VotingManager(Lobby lobby) {
		this.lobby = lobby;
		this.main = lobby.main;
		
		//Load 3 random arenas
		ArrayList<Arena> arenas = new ArrayList<Arena>(main.getArenaManager().arenas.values());
		Collections.shuffle(arenas);
		if (arenas.size() >= 1) {
			vote1 = arenas.get(0);
		}
		if (arenas.size() >= 2) {
			vote2 = arenas.get(1);
		}
		if (arenas.size() >= 3) {
			vote3 = arenas.get(2);
		}
	}
	
	public void openInventory (Player p) {
		if (vote1 == null) return;
		Inventory inv = Bukkit.createInventory(null, 9, Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.title")));
		
		ItemStack votestack1 = new ItemStack(vote1.displayitem);
		ItemMeta meta1 = votestack1.getItemMeta();
		meta1.setDisplayName(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.item-title").replace("%display%", vote1.display)));
		ArrayList<String> lore1 = new ArrayList<String>();
		lore1.add(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.item-lore").replace("%votes%", this.calcVotes(1) + "")));
		meta1.setLore(lore1);
		votestack1.setItemMeta(meta1);
		inv.setItem(2, votestack1);
		
		if (vote2 != null) {
			ItemStack votestack2 = new ItemStack(vote2.displayitem);
			ItemMeta meta2 = votestack2.getItemMeta();
			meta2.setDisplayName(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.item-title").replace("%display%", vote2.display)));
			ArrayList<String> lore2 = new ArrayList<String>();
			lore2.add(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.item-lore").replace("%votes%", this.calcVotes(2) + "")));
			meta2.setLore(lore2);
			votestack2.setItemMeta(meta2);
			inv.setItem(4, votestack2);
		}
		
		if (vote3 != null) {
			ItemStack votestack3 = new ItemStack(vote3.displayitem);
			ItemMeta meta3 = votestack3.getItemMeta();
			meta3.setDisplayName(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.item-title").replace("%display%", vote3.display)));
			ArrayList<String> lore3 = new ArrayList<String>();
			lore3.add(Main.c(main.getSettingsFile().getConfiguration().getString("game.inventory.voting.item-lore").replace("%votes%", this.calcVotes(3) + "")));
			meta3.setLore(lore3);
			votestack3.setItemMeta(meta3);
			inv.setItem(6, votestack3);
		}
		p.openInventory(inv);
	}
	
	public void vote(Player p , int arena) {
		this.voted.put(p, arena);
		Arena voted = vote1;
		if (arena == 2) {
			voted = vote2;
		}
		if (arena == 3) {
			voted = vote3;
		}
		p.sendMessage(main.getMessageFetcher().getMessage("game.vote.voted", true).replace("%display%", voted.display));
	}
	
	public int calcVotes(int arena) {
		int votes = 0;
		for (Player p : this.voted.keySet()) {
			if (lobby.contains(p)) {
				if (voted.containsKey(p)) {
					if (voted.get(p) == arena) {
						votes ++;
					}
				}
			}
		}
		return votes;
	}
	
	public Arena winner() {
		Arena winner = vote1;
		if (this.calcVotes(1) < this.calcVotes(2)) {
			winner = vote2;
		}
		if (this.calcVotes(2) < this.calcVotes(3)) {
			winner = vote3;
		}
		return winner;
	}
	
	public void click(Player p, int slot) {
		if (slot == 2) vote(p , 1);
		if (slot == 4) vote(p , 2);
		if (slot == 6) vote(p , 3);
		p.closeInventory();
	}

	
	
}
