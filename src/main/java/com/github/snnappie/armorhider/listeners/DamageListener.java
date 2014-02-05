package com.github.snnappie.armorhider.listeners;

import com.github.snnappie.armorhider.ArmorHider;
import com.github.snnappie.armorhider.ArmorHider.ArmorPiece;
import com.github.snnappie.armorhider.commands.HideCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageListener implements Listener {

	private ArmorHider plugin;
	
	public DamageListener(ArmorHider plugin) {
		this.plugin = plugin;
	}

	@EventHandler(ignoreCancelled=true)
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
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

                // reveal their armor - if they have the autohide perm, re-hide it automatically.
                if (plugin.isPlayerHidingArmor(player)) {
                    final List<ItemStack> armor = plugin.getHiddenArmor(player);
                    plugin.showArmor(player, ArmorPiece.ALL);
                    if (player.hasPermission(ArmorHider.PERM_AUTO)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (armor.size() == 4)
                                    plugin.hideArmor(player, ArmorPiece.ALL);
                                else
                                    for (ItemStack i : armor)
                                        plugin.hideArmor(player, ArmorHider.getArmorType(i));
                            }
                        }.runTask(plugin);
                    } else {
                        HideCommand.informPlayer(player, HideCommand.CommandType.SHOWARMOR);
                    }
                }

                if (plugin.isPlayerHidingEnchantments(player)) {
                    final HashMap<ItemStack, Map<Enchantment, Integer>> enchantments = plugin.getAllEnchantments(player);
                    plugin.showEnchantments(player, ArmorPiece.ALL);
                    if (player.hasPermission(ArmorHider.PERM_AUTO)) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (enchantments.size() == 4)
                                    plugin.hideEnchantments(player, ArmorPiece.ALL);
                                else
                                    for (ItemStack i : enchantments.keySet())
                                        plugin.hideEnchantments(player, ArmorHider.getArmorType(i));
                            }
                        }.runTask(plugin);
                    } else {
                        HideCommand.informPlayer(player, HideCommand.CommandType.SHOWENCHANT);
                    }
                }
				// let it fall through to the default, allowing for any damage that kills the player to properly remove
                // armor
//			default:

				// dealt enough damage to kill the player
//				if (event.getDamage() >= player.getHealth()) {
//                    System.out.println(event.getDamage() + " " + player.getHealth());
//					if (plugin.isPlayerHidingArmor(player))
//                        plugin.showArmor(player, ArmorPiece.ALL);
//                    if (plugin.isPlayerHidingEnchantments(player))
//                        plugin.showEnchantments(player, ArmorPiece.ALL);
//				}
			}
		}
	}

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (plugin.isPlayerHidingArmor(player)) {
            // simply revealing it doesn't work for armor, but works for enchantments for some reason
            for (ItemStack i : plugin.getHiddenArmor(player)) {
                player.getWorld().dropItem(player.getLocation(), i);
            }
            plugin.showArmor(player, ArmorPiece.ALL);
            HideCommand.informPlayer(player, HideCommand.CommandType.SHOWARMOR);
        } else if (plugin.isPlayerHidingEnchantments(player)) {
            plugin.showEnchantments(player, ArmorPiece.ALL);
            HideCommand.informPlayer(player, HideCommand.CommandType.SHOWENCHANT);
        }
    }
}
