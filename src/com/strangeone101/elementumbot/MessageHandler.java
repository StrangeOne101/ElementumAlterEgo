package com.strangeone101.elementumbot;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.strangeone101.elementumbot.command.Command;
import com.strangeone101.elementumbot.command.LinkCommand;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.DiscordUtil;
import com.strangeone101.elementumbot.util.StringUtil;
import com.strangeone101.elementumbot.util.StringUtil.Direction;
import com.strangeone101.elementumbot.util.StringUtil.WordType;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageHandler {

	public static void handle(Message message, DiscordAPI api) {
		if (ConfigManager.getBarredUsers().contains(message.getAuthor().getId())) {
			return;
		}
		
		if (message.getContent().startsWith("!")) {
			new Command(message);
		} else if (message.getChannelReceiver() != null && message.getChannelReceiver().getId() != null && //If the channel doesn't exist (PMs), ignore it
				message.getChannelReceiver().getId().equals(ConfigManager.getRelayChannel()) && message.getAuthor() != api.getYourself()) {
			if (!message.getContent().startsWith("!") && !message.getContent().startsWith("#")) {
				String displayName = message.getAuthor().getNickname(AlterEgoPlugin.SERVER) == null ? message.getAuthor().getName() : message.getAuthor().getNickname(AlterEgoPlugin.SERVER);
				
				Role role = DiscordUtil.getTopRole(message.getAuthor());
				String roleDisplay = ChatColor.DARK_GRAY + "No Role"; //If they have no role, this is the default text that shows
				
				if (role != null) {
					roleDisplay = DiscordUtil.getColorOfRole(role) + role.getName(); //The name of the role with the color converted to MC color codes
				}
				
				//The message is formatted to the one defined in the config. We replace name with either their nickname or username 
				String sentMessage = ConfigManager.getSayCommandFormat().replace("<name>", displayName).replace("<message>", message.getContent());
				String hoverText = ChatColor.GRAY + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + "\n" + roleDisplay;
				if (LinkCommand.isLinked(message.getAuthor().getId())) {
					hoverText = hoverText + "\n" + ChatColor.GRAY + "IGN: " + Bukkit.getOfflinePlayer(LinkCommand.getUUIDFromID(message.getAuthor().getId())).getName();
				}
				
				BaseComponent sentMessageComponent = new TextComponent("");
				BaseComponent[] componentLine = TextComponent.fromLegacyText(sentMessage); //Fixes the overflow of the color not working
				for (BaseComponent component : componentLine) {
					sentMessageComponent.addExtra(component);
				}
				
				sentMessageComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(hoverText)));
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.spigot().sendMessage(sentMessageComponent);
				}
				AlterEgoPlugin.INSTANCE.getLogger().info(sentMessage); //Just so it still shows in console
				//Bukkit.broadcastMessage(ConfigManager.getSayCommandFormat().replace("<name>", message.getAuthor().getName()).replace("<message>", message.getContent()));
				//LearningChatbot.INSTANCE.intake(message.getContent(), null);
				//DISABLED UNTIL FURTHER NOTICE
			}
			
		} else {
			//TODO parse things and compare to regex statements
		}
		
	}
	
	public static String format(String string) {
		return string.replaceAll("\\*", "\\\\*").replaceAll("_", "\\\\_").replaceAll("@everyone", "@ everyone").replaceAll("@here", "@ here");
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
					for (UUID uuid : LinkCommand.links.keySet()) {
						String id = LinkCommand.links.get(uuid);
						OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
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
