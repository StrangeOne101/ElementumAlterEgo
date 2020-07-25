package com.strangeone101.elementumbot.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import org.javacord.api.entity.user.User;

public class WhoIsCommand extends CommandRunnable {
	
	public WhoIsCommand() {
		super("whois");
	}

	@Override
	public void runCommand(Command command) {
		if (command.getArguments().length == 0) {
			command.reply("Error: Usage is `!whois user`!");
			return;
		}
		
		if (command.getOriginal().getMentionedUsers().size() > 0) {
			User user = command.getOriginal().getMentionedUsers().get(0);
			
			if (!LinkCommand.links.containsValue(user.getId())) {
				command.reply("Error: User has not linked their discord account to MC!");
				return;
			}
			
			for (UUID id : LinkCommand.links.keySet()) {
				if (LinkCommand.links.get(id).equals(user.getId())) {
					OfflinePlayer player = Bukkit.getOfflinePlayer(id);
					command.reply(user.getName() + " is " + player.getName() + " in game.");
					return;
				}
			}

			command.reply("Error: Discord dun goofed! How the hell did this happen???");
		} else {
			int matchID = -1;
			User user = null;
			if (command.getArguments()[0].contains("#")) {
				try {
					matchID = Integer.parseInt(command.getArguments()[0].split("#")[1]);
					command.getArguments()[0] = command.getArguments()[0].split("#")[0];
				} catch (NumberFormatException e) {};
			}
			for (User user2 : AlterEgoPlugin.SERVER.getMembers()) {
				if (matchID != -1 && user2.getDiscriminator().equals(matchID + "") && command.getArguments()[0].equalsIgnoreCase(user2.getName())) {
					user = user2;
					break;
				} else if (command.getArguments()[0].equalsIgnoreCase(user2.getName())) {
					user = user2;
				}
			}
			if (user == null) {
				//Loop again, but this time using .startsWith
				for (User user2 : AlterEgoPlugin.SERVER.getMembers()) {
					if (user2.getName().startsWith(command.getArguments()[0])) {
						user = user2;
						break;
					}
				}
			}
			if (user == null) { //If it's STILL null...
				command.reply("Error: User not found!");
			} else {
				if (!LinkCommand.links.containsValue(user.getId())) {
					command.reply("Error: User has not linked their discord account to MC!");
					return;
				}
				
				for (UUID id : LinkCommand.links.keySet()) {
					if (LinkCommand.links.get(id).equals(user.getId())) {
						OfflinePlayer player = Bukkit.getOfflinePlayer(id);
						command.reply(user.getName() + " is " + player.getName() + " in game.");
						return;
					}
				}
			}
		}
		
		

		
	}

}
