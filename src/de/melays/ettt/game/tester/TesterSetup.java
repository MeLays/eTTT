package de.melays.ettt.game.tester;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.ettt.Main;
import de.melays.ettt.tools.Tools;

public class TesterSetup implements Listener{
	
	Main main;
	HashMap<Player , String> players = new HashMap<Player,String>();
	
	public TesterSetup (Main m){
		main = m;
	}
	
	//MODIFIED VERSION OF NEWTESTERSETUP.JAVA FROM MTTT
	
	public void giveTools(Player p , String arena){
		
		ItemStack lamp = new ItemStack (Material.BLAZE_ROD);
		ItemMeta metalamp = lamp.getItemMeta();
		metalamp.setDisplayName(main.prefix + ChatColor.YELLOW+"Tester Tool");
		lamp.setItemMeta(metalamp);
		
		ItemStack selec = new ItemStack (Material.BLAZE_ROD);
		ItemMeta selecm = lamp.getItemMeta();
		selecm.setDisplayName(main.prefix + ChatColor.RED+"Area Selection Tool");
		selec.setItemMeta(selecm);
		p.getInventory().addItem(selec);
		
		p.getInventory().addItem(lamp);
		if (!players.containsKey(p)){
			players.put(p,arena);
		}
		else{
			players.remove(p);
			players.put(p,arena);
		}
		p.sendMessage(main.prefix + ChatColor.YELLOW + "Tester Tool" + ChatColor.GRAY + " Instructions:");
		p.sendMessage(main.prefix + "Rightclick every Block you want your tester to use.");
		p.sendMessage(main.prefix + ChatColor.RED + "Blocks: ----------------");
		p.sendMessage(main.prefix + "REDSTONE_LAMP --> Will light up if the testing Player is a Traitor");
		p.sendMessage(main.prefix + "STONE_BUTTON --> Used to use the tester as a player");
		p.sendMessage(main.prefix + "DIAMOND_BLOCK --> Will change to REDSTONE_BLOCK if the Tester is in use.");
		p.sendMessage(main.prefix + ChatColor.RED + "Leftclick will remove the Block!");
		p.sendMessage(main.prefix + ChatColor.GREEN + "Area Selection Tool" + ChatColor.GRAY + " Instructions:");
		p.sendMessage(main.prefix + "Rightclick and Leftclick the borders of the tester.");
		p.sendMessage(main.prefix + "No Players will be able to enter this area while a player is testing.");
		p.sendMessage(main.prefix + "At the end use /ttt-setup tester setinner/setouter.");
	}
	
	HashMap<Player , LocationMarker> markers = new HashMap<Player , LocationMarker>();
	
	@EventHandler
	public void interactEvent (PlayerInteractEvent e){
		if (e.getHand() == null) return;
		if (e.getHand() != EquipmentSlot.HAND) return;
		if (e.useInteractedBlock() == Result.DENY || e.useItemInHand() == Result.DENY) return;
		try{
			if (e.getPlayer().hasPermission("ttt.setup")){
				if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(main.prefix + ChatColor.RED+"Area Selection Tool")){
					return;
				}
				if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)){
					
					if (!markers.containsKey(e.getPlayer())){
						markers.put(e.getPlayer(), new LocationMarker(e.getPlayer()));
					}
					
					if (e.getAction() == Action.LEFT_CLICK_BLOCK){
						markers.get(e.getPlayer()).setLeft(e.getClickedBlock().getLocation());
						e.getPlayer().sendMessage(main.prefix + ChatColor.RED + "Left Location set succesfully! (arena="+players.get(e.getPlayer())+")");
					}
					else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
						markers.get(e.getPlayer()).setRight(e.getClickedBlock().getLocation());
						e.getPlayer().sendMessage(main.prefix + ChatColor.RED + "Right Location set succesfully! (arena="+players.get(e.getPlayer())+")");
					}
					e.setUseInteractedBlock(Result.DENY);
					e.setUseItemInHand(Result.DENY);
					if (markers.get(e.getPlayer()).complete()){
						generatePositions(markers.get(e.getPlayer()).locl , markers.get(e.getPlayer()).locr , e.getPlayer());
						markers.put(e.getPlayer(), new LocationMarker(e.getPlayer()));
					}
				}
			}
		}catch(Exception ex){return;}
	}
	
	public void generatePositions (Location l1 , Location l2 , Player p){
		//Pos1 needs smaller X and smaller Y
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
		Tools.saveLiteLocation(main.getArenaManager().getConfiguration(), players.get(p) + ".tester_data.corner_small" , new Location (l1.getWorld() , xpos1 , ypos1 , zpos1));
		Tools.saveLiteLocation(main.getArenaManager().getConfiguration(), players.get(p) + ".tester_data.corner_big" , new Location (l2.getWorld() , xpos2 , ypos2 , zpos2));

		main.getArenaManager().saveFile();
		p.sendMessage(main.prefix + "The Corners of the Tester have been calculated and saved.");
	}
	
	public int clearBlocks(String arena , Location loc){
		Set<String> blocks;
		try{
			blocks = main.getArenaManager().getConfiguration().getConfigurationSection(arena+".tester_data.blocks").getKeys(false);
		}catch(Exception ex){return 0;}
		if (blocks == null)return 0;
		int removed = 0;
		for (String s : blocks){
			Location loct = Tools.getLiteLocation(main.getArenaManager().getConfiguration(), arena+".tester_data.blocks."+s);
			if (loct.getBlock().getLocation().equals(loc.getBlock().getLocation())){
				main.getArenaManager().getConfiguration().set(arena+".tester_data.blocks."+s, null);
				removed ++;
			}
		}
		return removed;
	}
	
	@EventHandler
	public void onInteract (PlayerInteractEvent e){
		if (e.getHand() == null) return;
		if (e.getHand() != EquipmentSlot.HAND) return;
		if (e.useInteractedBlock() == Result.DENY || e.useItemInHand() == Result.DENY) return;
		try{
			Player p= e.getPlayer();
			if (!e.getPlayer().hasPermission("ttt.setup")) return;
			if (players.containsKey(e.getPlayer())){
				if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD){
						if (!e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(main.prefix + ChatColor.YELLOW+"Tester Tool"))return;
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
							clearBlocks(players.get(p) , e.getClickedBlock().getLocation());
							Tools.saveLiteLocation(main.getArenaManager().getConfiguration(), players.get(p)+".tester_data.blocks."+UUID.randomUUID(), e.getClickedBlock().getLocation());
							p.sendMessage(main.prefix + "Saved the " + e.getClickedBlock().getType() + " to the config.yml");
						}
						else{
							p.sendMessage(main.prefix + "Removed " + clearBlocks(players.get(p) , e.getClickedBlock().getLocation()) + " Blocks from the config!");
						}
						main.getArenaManager().saveFile();
						e.setUseInteractedBlock(Result.DENY);
						e.setUseItemInHand(Result.DENY);
				}
			}
		}catch(Exception ex){ex.printStackTrace();}
	}
	
}
