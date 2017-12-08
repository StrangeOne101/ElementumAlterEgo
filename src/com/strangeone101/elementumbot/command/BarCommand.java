package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Reactions;

import de.btobastian.javacord.entities.User;

public class BarCommand extends CommandRunnable {
	
	public BarCommand() {
		super("bar");
	}

	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		if (command.getOriginal().getMentions().isEmpty()) {
			command.getOriginal().reply("Invalid usage! Usage is `!bar @user`");
			return;
		}
		
		for (User user : command.getOriginal().getMentions()) {
			if (ConfigManager.getOps().contains(user.getId())) continue;
			ConfigManager.addBarredUser(user.getId());
		}
		command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
	}

}
