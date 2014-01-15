package com.github.snnappie.armorhider.listeners;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.github.snnappie.armorhider.ArmorHider;
import com.github.snnappie.armorhider.ArmorHider.ArmorPiece;
import com.github.snnappie.armorhider.commands.HideCommand;

public class DamageListener implements Listener {

	private ArmorHider plugin;
	
	public DamageListener(ArmorHider plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (!plugin.isPlayerHidingArmor(player) && !plugin.isPlayerHidingEnchantments(player))
				return;

			switch (event.getCause()) {
			// damages that are protected by armor
			case CONTACT:
			case ENTITY_ATTACK:
			case ENTITY_EXPLOSION:
			case FIRE:
			case LAVA:
			case FIRE_TICK:
			case BLOCK_EXPLOSION:
			case PROJECTILE:
				break;
				
			case FALL:
                // TODO: put this on a local branch or something - doesn't belong in release.
//				if (plugin.portalStickEnabled) {
//					if (plugin.getHiddenArmor(player) != null) {
//						List<ItemStack> armor = plugin.getHiddenArmor(player);
//						for (ItemStack i : armor) {
//							if (i.getType() == Material.DIAMOND_BOOTS) {
//								event.setCancelled(true);
//								return;
//							}
//						}
//					}
//
//					if ((player.getInventory().getBoots() != null) && player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS)
//						return;
//				}
				
				// special case: feather fall enchantments might be hidden
				Map<Enchantment, Integer> bootsEnchantments = plugin.getHiddenEnchantment(player, player.getInventory().getBoots());
				if (bootsEnchantments != null && bootsEnchantments.containsKey(Enchantment.PROTECTION_FALL)) {
					plugin.showEnchantments(player, ArmorPiece.BOOTS);
					HideCommand.informPlayer(player, HideCommand.CommandType.SHOWENCHANT);
				}
				
			default:

				// dealt enough damage to kill the player
				if (event.getDamage() >= player.getHealth()) {
					break;
				}
				return;
			}
			
			if (plugin.isPlayerHidingArmor(player)) {
				plugin.showArmor(player, ArmorPiece.ALL);
				HideCommand.informPlayer(player, HideCommand.CommandType.SHOWARMOR);
			}
			if (plugin.isPlayerHidingEnchantments(player)) {
				plugin.showEnchantments(player, ArmorPiece.ALL);
				HideCommand.informPlayer(player, HideCommand.CommandType.SHOWENCHANT);
			}
			
		}
	}
}
