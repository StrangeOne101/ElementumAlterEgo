package com.strangeone101.elementumbot.command;

import java.util.Arrays;

import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Reactions;

public class RelayCommand extends CommandRunnable {

	public RelayCommand() {
		super("relay");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		
		String[] validations = new String[] {"on", "off", "enable", "disable"};
		
		if (command.getArguments().length == 0 || !Arrays.asList(validations).contains(command.getArguments()[0].toLowerCase())) {
			command.getOriginal().reply("Usage is `!relay <on|off>`");
			return;
		} else if (Arrays.asList(validations).indexOf(command.getArguments()[0].toLowerCase()) % 2 == 0) { //If it's on/enable
			if (ConfigManager.getRelay()) command.getOriginal().reply("Relay is already enabled!");
			else {
				ConfigManager.setRelay(true);
				command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
			}
		} else { //If it's off/disable
			if (!ConfigManager.getRelay()) command.getOriginal().reply("Relay is already disabled!");
			else {
				ConfigManager.setRelay(false);
				command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
			}
		}
		
		

	}

}
