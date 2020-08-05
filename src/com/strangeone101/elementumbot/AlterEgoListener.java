package com.strangeone101.elementumbot;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
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

import com.strangeone101.elementumbot.command.LinkCommand;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.config.MatchesManager;
import com.strangeone101.elementumbot.elementum.RankSync;
import com.strangeone101.elementumbot.util.Reactions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class AlterEgoListener implements Listener {
	
	public AlterEgoListener() {
		LuckPermsProvider.get().getEventBus().subscribe(UserDataRecalculateEvent.class, this::onRankChange);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled() || !ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		
		String name = MessageHandler.format(event.getPlayer().getDisplayName().replace(Reactions.LEFT_CURLY_BRACE, '<')
				.replace(Reactions.RIGHT_CURLY_BRACE, '>'));
		
		String message = MessageHandler.format(MessageHandler.tagRelayUsers(event.getMessage()));
		
		AlterEgoPlugin.relay(ConfigManager.getRelayFormat().replace("%player%", event.getPlayer().getName()).replace("%message%", message));
		
		new BukkitRunnable() {

			@Override
			public void run() {
				BaseComponent component = MatchesManager.getMatch(event.getMessage(), event.getPlayer());
				if (component != null) {
					TextComponent reply = new TextComponent(AlterEgoPlugin.PREFIX + " " + ChatColor.RED);
					reply.addExtra(component);
					AlterEgoPlugin.relay(reply.toLegacyText());
					for (Player player : Bukkit.getOnlinePlayers()) {
						//Send message. Async is fine.
						
						//Messages on the MC server need to be sent in a synced
						//thread and can't be async
						new BukkitRunnable() {
							@Override
							public void run() {
								player.spigot().sendMessage(reply);
							}
						}.runTask(AlterEgoPlugin.INSTANCE);
					}
				}
				
				//Let the bot think about a response async.
				//DISABLED UNTIL FURTHER NOTICE
				//LearningChatbot.INSTANCE.intake(event.getMessage(), event.getPlayer());
			}
			
		}.runTaskLaterAsynchronously(AlterEgoPlugin.INSTANCE, 10);
	}
	
	public void onRankChange(UserDataRecalculateEvent event) {
		if (LinkCommand.isLinked(event.getUser().getUniqueId())) {
			RankSync.syncRank(event.getUser());
			//AlterEgoPlugin.INSTANCE.getLogger().info("Recalculated user " + event.getUser().getFriendlyName());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		if (!ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		AlterEgoPlugin.relay(MessageHandler.format(event.getQuitMessage()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		if (!ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		AlterEgoPlugin.relay(MessageHandler.format("[+] " + event.getPlayer().getName())); //For some reason, event.getJoinMessage() is null. Fucks sake.
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		if (!ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		AlterEgoPlugin.relay(MessageHandler.format(event.getDeathMessage()));
	}
}
