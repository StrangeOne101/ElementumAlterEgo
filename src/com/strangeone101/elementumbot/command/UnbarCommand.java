package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Reactions;
import org.javacord.api.entity.user.User;

public class UnbarCommand extends CommandRunnable {

	public UnbarCommand() {
		super("unbar");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		if (command.getOriginal().getMentionedUsers().isEmpty()) {
			command.reply("Invalid usage! Usage is `!unbar @user`");
			return;
		}
		
		for (User user : command.getOriginal().getMentionedUsers()) {
			ConfigManager.removeBarredUser(user.getId());
		}
		command.getOriginal().addReaction(Reactions.GREEN_TICK + "");
	}

}
