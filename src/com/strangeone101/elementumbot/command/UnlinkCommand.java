package com.strangeone101.elementumbot.command;

import org.bukkit.Bukkit;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.Reactions;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.elementum.RankSync;

import de.btobastian.javacord.entities.User;

public class UnlinkCommand extends CommandRunnable {
	
	public UnlinkCommand() {
		super("unlink");
	}

	@Override
	public void runCommand(Command command) {
		if (!LinkCommand.links.containsKey(command.getSender().getId()) || (!command.hasOppedPower() && command.getArguments().length != 0)) {
			command.getOriginal().reply("Cannot unlink an account that is not already linked!");
			return;
		}
		
		if (command.hasOppedPower() && command.getArguments().length > 0) {
			if (command.getOriginal().getMentions().size() == 0) {
				command.getOriginal().reply("Usage is `!unlink @user` if you want to unlink another persons account!");
			} else {
				for (User user : command.getOriginal().getMentions()) {
					if (LinkCommand.links.containsKey(user.getId())) {
						AlterEgoPlugin.INSTANCE.getLogger().info("Discord user " + user.getName() + "(" + user.getMentionTag() + ") unlinked with MC user " + Bukkit.getOfflinePlayer(LinkCommand.links.get(user.getId())).getName());
						LinkCommand.links.remove(user.getId());
						RankSync.donorRole.removeUser(user);
						command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
					} else {
						command.getOriginal().addUnicodeReaction(Reactions.RED_CROSS + "");
					}
				}
			}
		} else {
			if (LinkCommand.links.containsKey(command.getSender().getId())) {
				AlterEgoPlugin.INSTANCE.getLogger().info("Discord user " + command.getSender().getName() + "(" + command.getSender().getMentionTag() + ") unlinked with MC user " + Bukkit.getOfflinePlayer(LinkCommand.links.get(command.getSender().getId())).getName());
				LinkCommand.links.remove(command.getSender().getId());
				RankSync.donorRole.removeUser(command.getSender());
				command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
			}
		}
		
		ConfigManager.save();
	}

}
