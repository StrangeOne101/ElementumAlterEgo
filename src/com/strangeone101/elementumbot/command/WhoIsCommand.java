package com.strangeone101.elementumbot.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.btobastian.javacord.entities.User;

public class WhoIsCommand extends CommandRunnable {
	
	public WhoIsCommand() {
		super("whois");
	}

	@Override
	public void runCommand(Command command) {
		if (command.getArguments().length == 0 || command.getOriginal().getMentions().size() == 0) {
			command.getOriginal().reply("Error: Usage is `!whois @user`!");
			return;
		}

		User user = command.getOriginal().getMentions().get(0);
		
		if (!LinkCommand.links.containsKey(user.getId())) {
			command.getOriginal().reply("Error: User has not linked their discord account to MC!");
			return;
		}
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(LinkCommand.links.get(user.getId()));
		command.getOriginal().reply(user.getName() + " is " + player.getName() + " in game.");
	}

}
