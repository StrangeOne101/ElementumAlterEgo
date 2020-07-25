package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Reactions;
import org.javacord.api.entity.user.User;

public class BarCommand extends CommandRunnable {
	
	public BarCommand() {
		super("bar");
	}

	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		if (command.getOriginal().getMentionedUsers().isEmpty()) {
			command.getOriginal().getChannel().sendMessage("Invalid usage! Usage is `!bar @user`");
			return;
		}
		
		for (User user : command.getOriginal().getMentionedUsers()) {
			if (ConfigManager.getOps().contains(user.getId())) continue;
			ConfigManager.addBarredUser(user.getId());
		}
		command.getOriginal().addReaction(Reactions.GREEN_TICK + "");
	}

}
