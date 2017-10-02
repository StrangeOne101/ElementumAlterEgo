package com.strangeone101.elementumbot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.strangeone101.elementumbot.config.ConfigManager;

import de.btobastian.javacord.entities.Channel;
import net.md_5.bungee.api.ChatColor;

public class AlterEgoListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled() || !ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		
		String message = MessageHandler.format(ChatColor.stripColor(event.getMessage()));
		String name = MessageHandler.format(ChatColor.stripColor(event.getPlayer().getDisplayName()
				.replace(Reactions.LEFT_CURLY_BRACE, '<')
				.replace(Reactions.RIGHT_CURLY_BRACE, '>')));
		
		Channel channel = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel());
		channel.sendMessage("[MCS] " + name + ": " + message);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (!ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		Channel channel = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel());
		channel.sendMessage("[MCS] " + MessageHandler.format(event.getPlayer().getName()) + " left the game");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		Channel channel = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel());
		channel.sendMessage("[MCS] " + MessageHandler.format(event.getPlayer().getName()) + " joined the game");
	}
}
