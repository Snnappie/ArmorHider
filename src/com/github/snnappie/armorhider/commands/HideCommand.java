package com.github.snnappie.armorhider.commands;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.snnappie.armorhider.ArmorHider;
import com.github.snnappie.armorhider.ArmorHider.ArmorPiece;


public class HideCommand implements CommandExecutor {

	private ArmorHider plugin;

	public HideCommand(ArmorHider plugin) {
		this.plugin = plugin;
	}

	// TODO review better ways to handle this
	private static String[][] armorStrings = { 
		{ "all" }, // 0
		{ "hat", "head", "helmet" }, // 1
		{ "chest", "body", "chestpiece", "chestplate" }, // 2
		{ "legs", "leggings" }, // 3
		{ "boots", "shoes", "feet" } // 4
	};
	
	
	private static enum CommandType {
		HIDEARMOR, SHOWARMOR, HIDEENCHANT, SHOWENCHANT;
	}

	
	public static ArmorPiece getArmorPieceFromString(String piece) {
		
		for (int i = 0; i < armorStrings.length; i++) {
			for (String pieceString : armorStrings[i]) {
				if (pieceString.equalsIgnoreCase(piece)) {
					switch (i) {
					case 0:
						return ArmorPiece.ALL;
					case 1:
						return ArmorPiece.HAT;
					case 2:
						return ArmorPiece.CHEST;
					case 3:
						return ArmorPiece.LEGS;
					case 4:
						return ArmorPiece.BOOTS;
					}
				}
			}
		}
		
		return null;
	}

	
	private void run(CommandType command, Player player, ArmorPiece piece) {
		
		switch (command) {
		case HIDEARMOR:
			plugin.hideArmor(player, piece);
			break;
		case SHOWARMOR:
			plugin.showArmor(player, piece);
			break;
		case HIDEENCHANT:
			System.out.println("Hide enchantments");
			break;
		case SHOWENCHANT:
			System.out.println("Show enchantments");
			break;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			sender.sendMessage("Must be a player!");
			return false;
		}
		
		// TODO change this if necessary
		if (args.length >= 3 || args.length <= 0) {
			player.sendMessage(ChatColor.RED + "Incorrect arguments!");
			return false;
		}
		
		
		CommandType command = null;
		ArmorPiece piece = null;
		if (cmd.getName().equalsIgnoreCase("hidearmor")) {
			command = CommandType.HIDEARMOR;
		} else if (cmd.getName().equalsIgnoreCase("showarmor")) {
			command = CommandType.SHOWARMOR;
		} else if (cmd.getName().equalsIgnoreCase("hideenchant")) {
			command = CommandType.HIDEENCHANT;
		} else if (cmd.getName().equalsIgnoreCase("showenchant")) {
			command = CommandType.SHOWENCHANT;
		}
		
		// trying to hide one piece of armor
		if (args.length == 1) {
			piece = getArmorPieceFromString(args[0]);
			if (piece == null) {
				player.sendMessage(ChatColor.RED + "Invalid argument: " + args[0]);
				return false;
			}
			
			// TODO adapt for enchantments
			if (!checkHidePerms(player, piece)) {
				return true;
			}
			
		} else if (args.length == 2) {
			// player is hiding all armor except for one piece
			if (args[0].equalsIgnoreCase("all") && args[1].startsWith("-")) {
				piece = getArmorPieceFromString(args[1].substring(1));
				if (piece == null) {
					player.sendMessage(ChatColor.RED + "Invalid argument: " + args[1]);
					return false;
				}
				
				// TODO adapt for enchantments
				if (!checkHidePerms(player, ArmorPiece.ALL)){
					return true;
				}
				
				// special case of hiding all but one piece
				// TODO handle enchantments
				if (command == CommandType.HIDEARMOR) {
					plugin.hideArmor(player, ArmorPiece.ALL);
					plugin.showArmor(player, piece);
					return true;
				} else if (command == CommandType.SHOWARMOR) {
					plugin.showArmor(player, ArmorPiece.ALL);
					plugin.hideArmor(player, piece);
					return true;
				}
				
			}
			
			// player subtracted from something that wasn't 'all'
			if (args[1].startsWith("-")) {
				return false;
			}
			
			// player is trying to hide another players armor
			if (!player.hasPermission("armorhider.hideother")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to hide other players armor!");
				return true;
			}
			
			player = Bukkit.getPlayer(args[0]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + args[0] + " is not online");
				return true;
			}
			
			piece = getArmorPieceFromString(args[1]);
			if (piece == null) {
				sender.sendMessage(ChatColor.RED + "Invalid argument: " + args[1]);
				return false;
			}
			
			if (!checkHidePerms(player, piece)) {
				return true;
			}
			
		}
		
		// if we got here, run the command
		run(command, player, piece);
		return true;
	}
	
	private boolean checkHidePerms(Player player, ArmorPiece piece) {

		
		// handle permissions
		switch (piece) {
		case HAT:
			if (!player.hasPermission("armorhider.hidehat") && !player.hasPermission("armorhider.hideall")) {
				player.sendMessage(ChatColor.RED + "You do not have permissions to hide your hat!");
				return false;
			}
			break;
		case CHEST:
			if (!player.hasPermission("armorhider.hidechest") && !player.hasPermission("armorhider.hideall")) {
				player.sendMessage(ChatColor.RED + "You do not have permissions to hide your chestplate!");
				return false;
			}
			break;
		case LEGS:
			if (!player.hasPermission("armorhider.hidelegs") && !player.hasPermission("armorhider.hideall")) {
				player.sendMessage(ChatColor.RED + "You do not have permissions to hide your leggings!");
				return false;
			}
			break;
		case BOOTS:
			if (!player.hasPermission("armorhider.hideboots") && !player.hasPermission("armorhider.hideall")) {
				player.sendMessage(ChatColor.RED + "You do not have permissions to hide your boots!");
				return false;
			}
			break;
		case ALL:
			if (!player.hasPermission("armorhider.hideall")) {
				player.sendMessage(ChatColor.RED + "You do not have permissions to hide all your armor!");
				return false;
			}
			break;
		}
		
		return true;
	}
}
