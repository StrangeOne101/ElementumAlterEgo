package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.Reactions;
import com.strangeone101.elementumbot.config.ConfigManager;

import de.btobastian.javacord.entities.User;

public class UnbarCommand extends CommandRunnable {

	public UnbarCommand() {
		super("unbar");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		if (command.getOriginal().getMentions().isEmpty()) {
			command.getOriginal().reply("Invalid usage! Usage is `!unbar @user`");
			return;
		}
		
		for (User user : command.getOriginal().getMentions()) {
			ConfigManager.removeBarredUser(user.getId());
		}
		command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
	}

}
