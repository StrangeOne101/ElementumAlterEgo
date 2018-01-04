package com.strangeone101.elementumbot.command;

public abstract class CommandRunnable {
	
	public CommandRunnable(String name) {
		Command.registerCommand(name, this);
	}
	
	public CommandRunnable(String name, String[] aliases) {
		Command.registerCommand(name, this);
		for (String cmd : aliases) {
			Command.registerCommand(cmd, this);
		}
	}
	
	public CommandRunnable() {} //Allows people to bypass the autoregister
	
	public abstract void runCommand(Command command);

}
