package com.strangeone101.elementumbot.command;

import org.javacord.api.entity.message.Message;

import java.util.concurrent.Future;

public class LatencyCommand extends CommandRunnable {

	public LatencyCommand() {
		super("latency");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		if (command.getArguments().length != 0) {
			command.reply("Invalid usage! Usage is !latency");
			return;
		}
		
		long time = System.currentTimeMillis();
		Future<Message> future = command.reply("Testing latency...");
		Message message = null;
		
		try {
			message = future.get();
		} catch (Exception e) {
			command.reply("An error occurred while waiting: " + e.getLocalizedMessage());
			return;
		}
		
		long latency = System.currentTimeMillis() - time;
		command.reply("Latency is " + latency + "ms!");
		if (message != null) {
			message.delete();
		}
	}

}
