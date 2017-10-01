package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.Reactions;

public class ReloadCommand extends CommandRunnable {

	public ReloadCommand() {
		super("reload");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		
		AlterEgoPlugin.INSTANCE.reloadConfig();
		
		command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
	}

}
