package com.github.snnappie.armorhider.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.snnappie.armorhider.ArmorHider;
import com.github.snnappie.armorhider.ArmorHider.ArmorPiece;

public class DamageListener implements Listener {

	private ArmorHider plugin;
	
	public DamageListener(ArmorHider plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (!plugin.isPlayerHidingArmor(player))
				return;

			switch (event.getCause()) {
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
				if (plugin.portalStickEnabled)
					if ((player.getInventory().getBoots() != null) && player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS)
						return;
			default:

				// dealt enough damage to kill the player
				if (event.getDamage() >= player.getHealth()) {
					plugin.showArmor(player, ArmorPiece.ALL);
				}
				return;
			}
			plugin.showArmor(player, ArmorPiece.ALL);
		}
	}
}
