package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.AlterEgoPlugin;

public class InfoCommand extends CommandRunnable {

	public InfoCommand() {
		super("info");
	}
	
	@Override
	public void runCommand(Command command) {
		command.getOriginal().reply("Alter Ego v" + AlterEgoPlugin.INSTANCE.getDescription().getVersion() + " made by StrangeOne101. Source can be found at http://bit.ly/2xUF0TS");
	}

}
