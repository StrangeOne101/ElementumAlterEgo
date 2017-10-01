package com.strangeone101.elementumbot;

import com.strangeone101.elementumbot.command.Command;
import com.strangeone101.elementumbot.config.ConfigManager;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;

public class MessageHandler {

	public static void handle(Message message, DiscordAPI api) {
		if (message.getContent().startsWith("!") && !ConfigManager.getBarredUsers().contains(message.getAuthor().getId())) {
			new Command(message);
		} else {
			//TODO parse things and compare to regex statements
		}
		
	}
	
	
	
	

}
