package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.util.Reactions;

public class ReloadCommand extends CommandRunnable {

	public ReloadCommand() {
		super("reload");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		
		AlterEgoPlugin.INSTANCE.reloadConfig();
		
		command.getOriginal().addReaction(Reactions.GREEN_TICK + "");
	}

}
