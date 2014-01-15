package com.github.snnappie.armorhider.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;

import com.github.snnappie.armorhider.ArmorHider;
import com.github.snnappie.armorhider.ArmorHider.ArmorPiece;

public class InventoryListener implements Listener {

	private ArmorHider plugin;
	public InventoryListener(ArmorHider plugin) {
		this.plugin = plugin;
	}
	
	// TODO this needs to be tweaked!
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onArmorEquip(InventoryClickEvent event) {
		
		// player clicked on the armor slot
		if (event.getSlotType() == SlotType.ARMOR || (event.isShiftClick() && event.getWhoClicked().equals(event.getInventory().getHolder()))) {
			
			// 39 -> hat
			// 38 -> chest
			// 37 -> legs
			// 36 -> boots
			int slot = event.getSlot();
			ArmorPiece piece = ArmorHider.getArmorType(event.isShiftClick() ? event.getCurrentItem() : event.getCursor());
			Player player = (Player) event.getWhoClicked();
			
			// check hidden enchantments first
			if (event.getSlotType() == SlotType.ARMOR) {
				ArmorPiece temp = ArmorHider.getArmorType(event.getCurrentItem());
				if (plugin.isPlayerHidingEnchantmentOnPiece(player, temp)) {
					plugin.showEnchantments(player, temp);
					return;
				}
			}
			
			// the player had something that wasn't armor selected
			if (piece == null)
				return;
			
			// if it was a shift click, assign slot to the appropriate value
			if (event.isShiftClick()) {
				switch (piece) {
				case HAT:
					slot = 39;
					break;
				case CHEST:
					slot = 38;
					break;
				case LEGS:
					slot = 37;
					break;
				case BOOTS:
					slot = 36;
					break;
				case ALL:
					break;
				}
			}
			

			
			// check if the player is hiding the armor piece they are attempting to equip
			if (!plugin.isPlayerHidingArmorPiece(player, piece))
				return;
			
			if (slot <= 39 && slot >= 36) {
				event.setCancelled(true);
				player.updateInventory();
				String msg = "You cannot equip";
				switch (piece) {
				case HAT: msg += " a helmet while also hiding one!"; break;
				case CHEST: msg += " a chestplate while also hiding one!"; break;
				case LEGS: msg += " leggings while also hiding them!"; break;
				case BOOTS: msg += " boots while also hiding them!"; break;
				case ALL: break;
				}
				player.sendMessage(ChatColor.RED + msg);
			}

		}
	}
}
