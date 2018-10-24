package com.strangeone101.elementumbot;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.util.concurrent.FutureCallback;
import com.strangeone101.elementumbot.ai.tasks.FlyGlitchDetector;
import com.strangeone101.elementumbot.command.Command;
import com.strangeone101.elementumbot.command.LinkCommand;
import com.strangeone101.elementumbot.commandmc.DiscordReportMCommand;
import com.strangeone101.elementumbot.commandmc.LinkMCommand;
import com.strangeone101.elementumbot.commandmc.UnlinkMCommand;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.config.MatchesManager;
import com.strangeone101.elementumbot.util.AliasRegistry;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.utils.ratelimits.RateLimitType;

public class AlterEgoPlugin extends JavaPlugin {
	
	public static AlterEgoPlugin INSTANCE;
	public static DiscordAPI API;
	public static Server SERVER;
	
	public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&2[&aAlterEgo&2]&r");
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		new ConfigManager(); //Set up config
		new MatchesManager();
		new AliasRegistry();
		
		Command.registerDefaultCommands();
		
		Bukkit.getPluginCommand("link").setExecutor(new LinkMCommand());
		Bukkit.getPluginCommand("unlink").setExecutor(new UnlinkMCommand());
		Bukkit.getPluginCommand("discordreport").setExecutor(new DiscordReportMCommand());
				
		new FlyGlitchDetector();
		
		if (!ConfigManager.isValid()) {
			try {
				throw new InvalidConfigurationException("Invalid token; cannot start!");
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return;
		}
		
		API = Javacord.getApi(ConfigManager.getToken(), true);
		
		API.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI api) {
                // register listener
                api.registerListener(new MessageCreateListener() {
                    @Override
                    public void onMessageCreate(DiscordAPI api, Message message) {
                        // check the content of the message
                        MessageHandler.handle(message, api);
                    }
                });
                
                AlterEgoPlugin.API.setIdle(false);
                
                if (!ConfigManager.isValidRelayChannel()) {
                	getLogger().severe("Relay channel not defined! Won't relay!");
                	return;
                }
                if (AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()) == null) {
                	getLogger().severe("Relay channel not found! Won't relay in game chat!");
                } else { //If the channel exists, prepare listener for relay 
                	getLogger().info("Relay channel found and working!");
                	
                	if (ConfigManager.isValidRelayChannel()) {
            			SERVER = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()).getServer();
            		}
                }
                
                //Register listener. Just won't be active unless relay channel is valid and working
                Bukkit.getPluginManager().registerEvents(new AlterEgoListener(), INSTANCE);
                
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
		
		API.setAutoReconnect(true);
		API.getRateLimitManager().addRateLimit(RateLimitType.SERVER_MESSAGE, 500L);
		
		getLogger().info("Alter Ego Bot Enabled!");
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {		
		if (!ConfigManager.isValidRelayChannel() || !ConfigManager.getRelay()) return;
		Channel channel = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel());
		channel.sendMessage("[MCS] " + "Server restarting!");
		
		API.disconnect();
		
		ConfigManager.save();
		
		super.onDisable();
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		AlterEgoPlugin.API.setIdle(true);
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
        for (String key : ConfigManager.linkConfig.get().getKeys(false)) {
			LinkCommand.links.put(key, UUID.fromString(ConfigManager.linkConfig.get().getString(key)));
		}
        
        AlterEgoPlugin.API.setIdle(false);
	}
	
	/**
	 * Reports something to the staff channel
	 * @param message The message
	 */
	public static void report(String message) {
		if (ConfigManager.isValidReportChannel()) {
			AlterEgoPlugin.API.getChannelById(ConfigManager.getReportChannel()).sendMessage("[Report] " +message);
		}
		AlterEgoPlugin.INSTANCE.getLogger().warning("[Report] " + message);
		
	}
}
