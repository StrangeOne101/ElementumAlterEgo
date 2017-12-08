package com.strangeone101.elementumbot.command;

import org.bukkit.Bukkit;

import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Reactions;

public class SayCommand extends CommandRunnable {

	public SayCommand() {
		super("say");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		String message = command.getOriginal().getContent().substring("!say ".length());
		message = message.trim();
		
		Bukkit.broadcastMessage(ConfigManager.getSayCommandFormat().replace("<name>", command.getSender().getName()).replace("<message>", message));
		command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
	}

}
