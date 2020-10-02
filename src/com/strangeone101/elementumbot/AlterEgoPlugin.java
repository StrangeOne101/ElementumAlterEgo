package com.strangeone101.elementumbot;

import java.io.File;
import java.util.UUID;

import com.strangeone101.elementumbot.ai.tasks.FlyGlitchDetector;
import com.strangeone101.elementumbot.ai.tasks.PlayTitleUpdater;
import com.strangeone101.elementumbot.chatbot.LearningChatbot;
import com.strangeone101.elementumbot.commandmc.AntiSpamMCommand;
import com.strangeone101.elementumbot.elementum.AdvancedBanSupport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.util.concurrent.FutureCallback;
import com.strangeone101.elementumbot.command.Command;
import com.strangeone101.elementumbot.command.LinkCommand;
import com.strangeone101.elementumbot.commandmc.AlterEgoCommand;
import com.strangeone101.elementumbot.commandmc.DiscordReportMCommand;
import com.strangeone101.elementumbot.commandmc.LinkMCommand;
import com.strangeone101.elementumbot.commandmc.UnlinkMCommand;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.config.MatchesManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.Javacord;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.ratelimit.LocalRatelimiter;
import org.javacord.api.util.ratelimit.Ratelimiter;

public class AlterEgoPlugin extends JavaPlugin {
	
	public static AlterEgoPlugin INSTANCE;
	public static DiscordApi API;
	public static Server SERVER;
	
	public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&2[&aAlterEgo&2]&r");
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		new ConfigManager(); //Set up config
		new MatchesManager(); 
		
		Command.registerDefaultCommands();
		
		Bukkit.getPluginCommand("link").setExecutor(new LinkMCommand());
		Bukkit.getPluginCommand("unlink").setExecutor(new UnlinkMCommand());
		Bukkit.getPluginCommand("discordreport").setExecutor(new DiscordReportMCommand());
		Bukkit.getPluginCommand("alterego").setExecutor(new AlterEgoCommand());
		Bukkit.getPluginCommand("antispam").setExecutor(new AntiSpamMCommand());

		new PlayTitleUpdater();
		
		new FlyGlitchDetector();

		if (Bukkit.getPluginManager().isPluginEnabled("AdvancedBan")) {
			new AdvancedBanSupport();
		}
		
		//new LearningChatbot(new File(getDataFolder(), "brain.dat"));
		
		if (!ConfigManager.isValid()) {
			try {
				throw new InvalidConfigurationException("Invalid token; cannot start!");
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return;
		}
		
		API = new DiscordApiBuilder().setToken(ConfigManager.getToken()).setGlobalRatelimiter(new LocalRatelimiter(10, 2)).login().join();

		if (setup()) {
			API.addMessageCreateListener(event -> {
				MessageHandler.handle(event.getMessage(), event.getApi());
			});

			//Register listener. Just won't be active unless relay channel is valid and working
			Bukkit.getPluginManager().registerEvents(new AlterEgoListener(), INSTANCE);
		}
		
		/*API.setAutoReconnect(true);
		API.getRateLimitManager().addRateLimit(RateLimitType.SERVER_MESSAGE, 500L);*/
		
		getLogger().info("Alter Ego Bot Enabled!");
		
		super.onEnable();
	}

	private boolean setup() {
		if (!ConfigManager.isValidRelayChannel()) {
			getLogger().severe("Relay channel not defined! Won't relay!");
			return false;
		}

		if (AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()) == null) {
			getLogger().severe("Relay channel not found! Won't relay in game chat!");
			return false;
		}

		getLogger().info("Relay channel found and working!");

		SERVER = API.getServers().stream().findFirst().get();

		return true;
	}

	@Override
	public void onDisable() {		
		if (ConfigManager.isValidRelayChannel() && ConfigManager.getRelay()) {
			Channel channel = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()).get();
			getLogger().info("Sending restart message to relay");
			channel.asTextChannel().get().sendMessage("[MCS] " + "Server restarting!");
		}

		getLogger().info("Disconnecting from discord");
		API.disconnect();
		
		ConfigManager.save();
		
		super.onDisable();
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		//AlterEgoPlugin.API.setIdle(true);
		ConfigManager.defaultConfig.loadConfig();
		MatchesManager.reloadMatches();
		
		if (!ConfigManager.isValidRelayChannel()) {
        	getLogger().severe("Relay channel not defined! Won't relay!");
        	return;
        }
        if (AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()) == null) {
        	getLogger().severe("Relay channel not found! Won't relay in game chat!");
        } else {
        	getLogger().info("Relay channel found and working!");
        }
        
        LinkCommand.links.clear();
        /*for (String key : ConfigManager.linkConfig.get().getKeys(false)) {
			LinkCommand.links.put(UUID.fromString(ConfigManager.linkConfig.get().getString(key)), Long.valueOf(key));
		}*/

		for (String key : ConfigManager.linkConfig.get().getKeys(false)) {
			LinkCommand.links.put(UUID.fromString(key), Long.valueOf(ConfigManager.linkConfig.get().getString(key)));
		}
        
        //AlterEgoPlugin.API.setIdle(false);
	}
	
	/**
	 * Reports something to the staff channel
	 * @param message The message
	 */
	public static void report(String message) {
		if (ConfigManager.isValidReportChannel()) {
			AlterEgoPlugin.API.getChannelById(ConfigManager.getReportChannel()).get().asTextChannel().get().sendMessage("[Report] " + MessageHandler.format(message));
		}
		AlterEgoPlugin.INSTANCE.getLogger().warning("[Report] " + message);
		
	}
	
	public static void relay(String message) {
		if (ConfigManager.isValidRelayChannel()) {
			message = ChatColor.stripColor(message);
			AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()).get().asTextChannel().get().sendMessage(message);
		}
	}
}
