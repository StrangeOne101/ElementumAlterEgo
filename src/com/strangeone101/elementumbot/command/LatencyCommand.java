package com.strangeone101.elementumbot.command;

import java.util.concurrent.Future;

import de.btobastian.javacord.entities.message.Message;

public class LatencyCommand extends CommandRunnable {

	public LatencyCommand() {
		super("latency");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		if (command.getArguments().length != 0) {
			command.getOriginal().reply("Invalid usage! Usage is !latency");
			return;
		}
		
		long time = System.currentTimeMillis();
		Future<Message> future = command.getOriginal().reply("Testing latency...");
		Message message = null;
		
		try {
			message = future.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		long latency = System.currentTimeMillis() - time;
		command.getOriginal().reply("Latency is " + latency + "ms!");
		if (message != null) {
			message.delete();
		}
	}

}
