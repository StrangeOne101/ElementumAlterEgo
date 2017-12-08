package com.strangeone101.elementumbot.commandmc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.strangeone101.elementumbot.AlterEgoPlugin;

public class DiscordReportMCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if (!(sender.hasPermission("alterego.command.discordreport"))) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
			return true;
		}
		String message = String.join(" ", args);
		AlterEgoPlugin.report(message);
		return true;
	}

}
