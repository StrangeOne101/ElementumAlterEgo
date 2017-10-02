package com.strangeone101.elementumbot.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.strangeone101.elementumbot.MessageHandler;

public class ListCommand extends CommandRunnable {

	public ListCommand() {
		super("list");
	}
	
	@Override
	public void runCommand(Command command) {
		String players = "";
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			players = players + ", " + MessageHandler.format(p.getName());
		}
		
		if (players.equals("")) players = "None";
		else players = players.substring(2);
		command.getOriginal().reply("Currently online: " + players);
	}

}
