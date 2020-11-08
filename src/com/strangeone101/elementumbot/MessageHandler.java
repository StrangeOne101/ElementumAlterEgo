package com.strangeone101.elementumbot;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.strangeone101.elementumbot.elementum.AdvancedBanSupport;
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

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;

public class MessageHandler {

	public static void handle(Message message, DiscordApi api) {
		if (ConfigManager.getBarredUsers().contains(message.getAuthor().getId())) {
			return;
		}

		if (!message.getAuthor().asUser().isPresent()) return; //From a non user? Whatever that is. Maybe webhooks or something.

		if (AdvancedBanSupport.isMutedInGame(message.getAuthor().getId())) {
			message.delete("User is muted in game");
			return;
		}

		if(message.getServerTextChannel().isPresent() && //If the message is in a server
				message.getServerTextChannel().get().getId() == ConfigManager.getSuggestionChannel()) { //and in the suggestion channel
			message.addReactions("\uD83D\uDC4D", "\uD83E\uDD37", "\uD83D\uDC4E"); //add reactions
		}
		
		if (message.getContent().startsWith("!")) {
			new Command(message);
		} else if (message.getServerTextChannel().isPresent() && //If the channel doesn't exist (PMs), ignore it
				message.getServerTextChannel().get().getId() == ConfigManager.getRelayChannel() && message.getAuthor().asUser().get() != api.getYourself()) {
			if (!message.getContent().startsWith("!") && !message.getContent().startsWith("#")) {
				String displayName = StringUtil.emojiTranslate(message.getAuthor().getDisplayName() == null ? message.getAuthor().getName() : message.getAuthor().getDisplayName()).trim();

				String roleDisplay = ChatColor.DARK_GRAY + "No Role"; //If they have no role, this is the default text that shows

				Role role = DiscordUtil.getTopRole(message.getAuthor().asUser().get());

				//AlterEgoPlugin.INSTANCE.getLogger().info(role.toString());

				if (!role.isEveryoneRole()) {
					roleDisplay = DiscordUtil.getColorOfRole(role) + role.getName(); //The name of the role with the color converted to MC color codes
				}
				
				//The message is formatted to the one defined in the config. We replace name with either their nickname or username 
				String sentMessage = ConfigManager.getSayCommandFormat().replace("<name>", DiscordUtil.getColorOfRole(role) + displayName).replace("<message>", StringUtil.emojiTranslate(message.getReadableContent()));
				String hoverText = ChatColor.GRAY + message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator().get() + "\n" + roleDisplay;
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
		return string.replaceAll("\\*", "\\\\*").replaceAll("_", "\\\\_").replaceAll("`", "\\`").replaceAll("\\|", "\\\\|").replaceAll("@everyone", "@ everyone").replaceAll("@here", "@ here");
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

	/**
	 * Tags users in discord from in game
	 * @param string
	 * @return
	 */
	public static String tagRelayUsers(String string) {
		Pattern pattern = Pattern.compile("(?<=[ ,]|^)(@{1}[A-z0-9_]{3,18})");
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			String name = matcher.group().substring(1);
			for (UUID uuid : LinkCommand.links.keySet()) {
				long id = LinkCommand.links.get(uuid);
				OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
				if (player != null && player.getName().equalsIgnoreCase(name) && player.hasPlayedBefore()) {
					string = string.replaceAll("@" + name, "<@" + id + ">");
					break;
				}
			}
		}

		return string;


		/*if (string.matches(".*( |\\B)@{1}[A-z0-9_]+.*")) {
			for (int i = 0; i < string.length(); i++) {
				int right = StringUtil.getBoundary(string, i, Direction.RIGHT, WordType.WORD_WITH_NUMERICS);
				if (right != i && right != -1 && right > i + 3 && right <= string.length()) {
					String name = string.substring(i + 1, right + 1);
					for (UUID uuid : LinkCommand.links.keySet()) {
						long id = LinkCommand.links.get(uuid);
						int l = (id + "").length();
						OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
						if (player != null && player.getName().equalsIgnoreCase(name) && player.hasPlayedBefore()) {
							string = StringUtil.replaceBetween(string, i, right, "<@" + id + ">");
							i += l + 3;
						}
					}
				}
			}
		}
		return string;*/
	}
	
	
	

}
