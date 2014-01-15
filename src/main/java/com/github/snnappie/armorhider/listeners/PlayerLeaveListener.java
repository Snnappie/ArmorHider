package com.github.snnappie.armorhider.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.snnappie.armorhider.ArmorHider;
import com.github.snnappie.armorhider.ArmorHider.ArmorPiece;

public class PlayerLeaveListener implements Listener {

	
	private ArmorHider plugin;
	
	public PlayerLeaveListener(ArmorHider plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		playerDisconnected(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		playerDisconnected(event.getPlayer());
	}
	
	private void playerDisconnected(Player player) {
		if (plugin.isPlayerHidingArmor(player)) {
			plugin.showArmor(player, ArmorPiece.ALL);
		}
		
		if (plugin.isPlayerHidingEnchantments(player)) {
			plugin.showEnchantments(player, ArmorPiece.ALL);
		}
	}
}
