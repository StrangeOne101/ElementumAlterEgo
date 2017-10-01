package com.strangeone101.elementumbot.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ListDCommand extends CommandRunnable {

	public ListDCommand() {
		super("listd");
	}
	
	@Override
	public void runCommand(Command command) {
		String players = "";
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			players = players + ", " + p.getDisplayName();
		}
		
		if (players.equals("")) players = "None";
		else players = players.substring(2);
		
		command.getOriginal().reply("Currently online: " + ChatColor.stripColor(players));
	}
}
