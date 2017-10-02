package com.strangeone101.elementumbot.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SayCommand extends CommandRunnable {

	public SayCommand() {
		super("say");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		String message = command.getOriginal().getContent().substring("!say ".length());
		message = message.trim();
		
		Bukkit.broadcastMessage(ChatColor.GRAY + "[Discord] " + command.getSender().getName() + ": " + message);
	}

}
