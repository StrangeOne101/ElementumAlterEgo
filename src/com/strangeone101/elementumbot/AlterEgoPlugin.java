package com.strangeone101.elementumbot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.util.concurrent.FutureCallback;
import com.strangeone101.elementumbot.command.Command;
import com.strangeone101.elementumbot.config.ConfigManager;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

public class AlterEgoPlugin extends JavaPlugin {
	
	public static AlterEgoPlugin INSTANCE;
	public static DiscordAPI API;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		new ConfigManager(); //Set up config
		
		Command.registerDefaultCommands();
		
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
                	Bukkit.getPluginManager().registerEvents(new AlterEgoListener(), INSTANCE);
                	getLogger().info("Relay channel found and working!");
                }
                
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
		
		getLogger().info("Alter Ego Bot Enabled!");
		
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		API.disconnect();
		super.onDisable();
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		AlterEgoPlugin.API.setIdle(true);
		ConfigManager.defaultConfig.loadConfig();
		
		if (!ConfigManager.isValidRelayChannel()) {
        	getLogger().severe("Relay channel not defined! Won't relay!");
        	return;
        }
        if (AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()) == null) {
        	getLogger().severe("Relay channel not found! Won't relay in game chat!");
        } else {
        	getLogger().info("Relay channel found and working!");
        }
        
        AlterEgoPlugin.API.setIdle(false);
	}
}
