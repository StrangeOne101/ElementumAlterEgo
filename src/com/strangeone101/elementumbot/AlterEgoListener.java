package com.strangeone101.elementumbot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.config.MatchesManager;
import com.strangeone101.elementumbot.util.Reactions;

import de.btobastian.javacord.entities.Channel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class AlterEgoListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled() || !ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		
		String message = MessageHandler.format(ChatColor.stripColor(event.getMessage()));
		String name = MessageHandler.format(ChatColor.stripColor(event.getPlayer().getDisplayName()
				.replace(Reactions.LEFT_CURLY_BRACE, '<')
				.replace(Reactions.RIGHT_CURLY_BRACE, '>')));
		
		Channel channel = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel());
		message = MessageHandler.tagUsers(message);
		
		channel.sendMessage("[MCS] " + name + ": " + message);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				BaseComponent component = MatchesManager.getMatch(event.getMessage());
				if (component != null) {
					TextComponent reply = new TextComponent(AlterEgoPlugin.PREFIX + " " + ChatColor.RED);
					reply.addExtra(component);
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.spigot().sendMessage(reply);
					}
				}
			}
			
		}.runTaskLater(AlterEgoPlugin.INSTANCE, 10);
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
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		if (!ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		Channel channel = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel());
		channel.sendMessage("[MCS] " + MessageHandler.format(event.getDeathMessage()));
	}
}
