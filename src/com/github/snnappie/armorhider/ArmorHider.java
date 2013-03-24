package com.github.snnappie.armorhider;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.snnappie.armorhider.commands.HideCommand;
import com.github.snnappie.armorhider.listeners.DamageListener;
import com.github.snnappie.armorhider.listeners.InventoryListener;
import com.github.snnappie.armorhider.listeners.PlayerLeaveListener;

public class ArmorHider extends JavaPlugin {

	private HashMap<Player, List<ItemStack>> hiddenArmor = new HashMap<Player, List<ItemStack>>();
	
	public boolean portalStickEnabled;
	public void onEnable() {
		portalStickEnabled = getServer().getPluginManager().getPlugin("PortalStick") != null;
		getCommand("hidearmor").setExecutor(new HideCommand(this));
		getCommand("showarmor").setExecutor(new HideCommand(this));
		
		getCommand("hideenchant").setExecutor(new HideCommand(this));
		getCommand("showenchant").setExecutor(new HideCommand(this));
		
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		getServer().getPluginManager().registerEvents(new PlayerLeaveListener(this), this);
		getServer().getPluginManager().registerEvents(new DamageListener(this), this);
	}
	
	public void onDisable() {
		// when the server stops, force all players to show their armor
		if (!hiddenArmor.isEmpty()) {
			Player[] players = Bukkit.getOnlinePlayers();
			for (Player player : players) {
				if (isPlayerHidingArmor(player))
					showArmor(player, ArmorPiece.ALL);
			}
		}
	}
	
	
	// hides the players armor
	public void hideArmor(Player player, ArmorHider.ArmorPiece piece) {
		PlayerInventory inventory = player.getInventory();
		ItemStack hat, chest, legs, boots;
		ArrayList<ItemStack> armorSet = new ArrayList<ItemStack>();

		hat = inventory.getHelmet();
		chest = inventory.getChestplate();
		legs = inventory.getLeggings();
		boots = inventory.getBoots();
		// TODO clean this up
		// test new ideas
		switch (piece) {
		case ALL:
			if (hat != null) {
				armorSet.add(hat);
				inventory.setHelmet(null);
			}
			if (chest != null) {
				armorSet.add(chest);
				inventory.setChestplate(null);
			}
			if (legs != null) {
				armorSet.add(legs);
				inventory.setLeggings(null);
			}
			if (boots != null) {
				armorSet.add(boots);
				inventory.setBoots(null);
			}
			break;
		case BOOTS:
			if (boots != null) {
				armorSet.add(boots);
				inventory.setBoots(null);
			}
			break;
		case HAT:
			if (hat != null) {
				armorSet.add(hat);
				inventory.setHelmet(null);
			}
			break;
		case LEGS:
			if (legs != null) {
				armorSet.add(legs);
				inventory.setLeggings(null);
			}
			break;
		case CHEST:
			if (chest != null) {
				armorSet.add(chest);
				inventory.setChestplate(null);
			}
			break;
		}

		addArmor(player, armorSet);

		player.sendMessage(ChatColor.RED + "Armor hidden!");
	}

	// reveals the players armor if it is hidden
	public void showArmor(Player player, ArmorHider.ArmorPiece piece) {
		if (!isPlayerHidingArmor(player))
			return;
		PlayerInventory inventory = player.getInventory();
		List<ItemStack> armorSet = removeArmor(player, piece);
		
		for (ItemStack armorPiece : armorSet) {
			switch (ArmorHider.getArmorType(armorPiece)) {
			case BOOTS:
				inventory.setBoots(armorPiece);
				break;
			case CHEST:
				inventory.setChestplate(armorPiece);
				break;
			case HAT:
				inventory.setHelmet(armorPiece);
				break;
			case LEGS:
				inventory.setLeggings(armorPiece);
				break;
			case ALL:
				break;
			}
		}
		
		player.sendMessage(ChatColor.RED + "Armor revealed!");
	}
	
	// adds the armor for the given player to the hidden armor list
	private void addArmor(Player player, List<ItemStack> armorSet) {
		if (armorSet.isEmpty())
			return;
		if (hiddenArmor.containsKey(player)) {
			// player is already hiding all of their armor
			if (hiddenArmor.get(player).size() == 4)
				return;
			
			// add the armor that isn't hidden
			for (ItemStack piece : armorSet) {
				if (!hiddenArmor.get(player).contains(piece))
					hiddenArmor.get(player).add(piece);
			}
				
		} else {
			hiddenArmor.put(player, armorSet);
		}
	}
	
	// removes the armor from hidden armor for the given player
	private List<ItemStack> removeArmor(Player player, ArmorPiece piece) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		if (hiddenArmor.get(player) == null)
			return ret;
		if (piece == ArmorPiece.ALL)
			return hiddenArmor.remove(player);
		
		// check for the armor piece
		for (int i = 0; i < hiddenArmor.get(player).size(); i++) {
			if (getArmorType(hiddenArmor.get(player).get(i)) == piece) {
				ret.add(hiddenArmor.get(player).remove(i));
				break;
			}
		}
		
		if (hiddenArmor.get(player).isEmpty())
			hiddenArmor.remove(player);
		return ret;
	}
	
	
	public boolean isPlayerHidingArmor(Player player) {
		return hiddenArmor.containsKey(player);
	}
	
	public boolean isPlayerHidingArmorPiece(Player player, ArmorPiece piece) {
		for (ItemStack armorPiece : hiddenArmor.get(player)) {
			if (getArmorType(armorPiece) == piece)
				return true;
		}
		
		return false;
	}
	
	// returns the enumerated type for the given piece of armor
	public static ArmorPiece getArmorType(ItemStack armor) {
		switch (armor.getType()) {
		case LEATHER_HELMET:
		case GOLD_HELMET:
		case CHAINMAIL_HELMET:
		case IRON_HELMET:
		case DIAMOND_HELMET:
			return ArmorPiece.HAT;
		case LEATHER_LEGGINGS:
		case GOLD_LEGGINGS:
		case CHAINMAIL_LEGGINGS:
		case IRON_LEGGINGS:
		case DIAMOND_LEGGINGS:
			return ArmorPiece.LEGS;
		case LEATHER_CHESTPLATE:
		case GOLD_CHESTPLATE:
		case CHAINMAIL_CHESTPLATE:
		case IRON_CHESTPLATE:
		case DIAMOND_CHESTPLATE:
			return ArmorPiece.CHEST;
		case LEATHER_BOOTS:
		case GOLD_BOOTS:
		case CHAINMAIL_BOOTS:
		case IRON_BOOTS:
		case DIAMOND_BOOTS:
			return ArmorPiece.BOOTS;
			default:
				return null;
		}
	}
	
	
	// enumerated type of each piece of armor, including all of them
	public static enum ArmorPiece {
		HAT, CHEST, LEGS, BOOTS, ALL;
	}
}
