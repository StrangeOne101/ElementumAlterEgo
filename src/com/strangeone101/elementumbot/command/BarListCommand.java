package com.strangeone101.elementumbot.command;

import java.util.List;

import com.strangeone101.elementumbot.config.ConfigManager;

public class BarListCommand extends CommandRunnable {

	public BarListCommand() {
		super("barlist");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		List<String> ids = ConfigManager.getBarredUsers();
		String newIDs = "";
		if (ids.isEmpty()) {
			newIDs = "No users are currently barred!";
		} else {
			for (String id : ids) {
				newIDs = newIDs + ", " + "<@" + id + ">";
			}
			newIDs = newIDs.substring(2);
			newIDs = "Barred Users: " + newIDs;
		}
		
		command.reply(newIDs);
	}

}
