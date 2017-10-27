package com.strangeone101.elementumbot;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.strangeone101.elementumbot.command.Command;
import com.strangeone101.elementumbot.command.LinkCommand;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.StringUtil;
import com.strangeone101.elementumbot.util.StringUtil.Direction;
import com.strangeone101.elementumbot.util.StringUtil.WordType;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;

public class MessageHandler {

	public static void handle(Message message, DiscordAPI api) {
		if (ConfigManager.getBarredUsers().contains(message.getAuthor().getId())) {
			return;
		}
		
		if (message.getContent().startsWith("!")) {
			new Command(message);
		} else if (message.getChannelReceiver().getId().equals(ConfigManager.getRelayChannel()) && message.getAuthor() != api.getYourself()) {
			if (!message.getContent().startsWith("!") && !message.getContent().startsWith("#")) {
				Bukkit.broadcastMessage(ConfigManager.getSayCommandFormat().replace("<name>", message.getAuthor().getName()).replace("<message>", message.getContent()));
			}
			
		} else {
			//TODO parse things and compare to regex statements
		}
		
	}
	
	public static String format(String string) {
		return string.replaceAll("\\*", "\\\\*").replaceAll("_", "\\\\_");
	}
	
	/*public static String toIngame(String string) {
		
	}*/
	
	/*public static String tagUsers(String string) {
		if (string.matches("( |\\B)@{1}[A-Z|a-z]+")) {
			for (int i = 0; i < string.length(); i++) {
				int right = StringUtil.getBoundary(string, i + 1, Direction.RIGHT, WordType.WORD_WITH_NUMERICS);
				if (right != i && right != -1 && right > i + 3 && right <= string.length()) {
					String name = string.substring(i + 1, right);
					for (String id : LinkCommand.links.keySet()) {
						OfflinePlayer player = Bukkit.getOfflinePlayer(LinkCommand.links.get(id));
						if (player != null && player.getName().equalsIgnoreCase(name) && player.hasPlayedBefore() && !player.isOnline()) {
							string = StringUtil.replaceBetween(string, i, right, "<@" + id + ">");
							i += id.length() + 3;
						}
					}
				}
			}
		}
		return string;
	}*/
	
	public static String tagUsers(String string) {
		if (string.matches(".*( |\\B)@{1}[A-Z|a-z]+.*")) {
			for (int i = 0; i < string.length(); i++) {
				int right = StringUtil.getBoundary(string, i, Direction.RIGHT, WordType.WORD_WITH_NUMERICS);
				if (right != i && right != -1 && right > i + 3 && right <= string.length()) {
					String name = string.substring(i + 1, right + 1);
					for (String id : LinkCommand.links.keySet()) {
						OfflinePlayer player = Bukkit.getOfflinePlayer(LinkCommand.links.get(id));
						if (player != null && player.getName().equalsIgnoreCase(name) && player.hasPlayedBefore() && !player.isOnline()) {
							string = StringUtil.replaceBetween(string, i, right, "<@" + id + ">");
							i += id.length() + 3;
						}
					}
				}
			}
		}
		return string;
	}
	
	
	

}
