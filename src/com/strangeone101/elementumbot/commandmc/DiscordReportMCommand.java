package com.strangeone101.elementumbot.commandmc;

import com.strangeone101.elementumbot.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.strangeone101.elementumbot.AlterEgoPlugin;

import java.util.Arrays;

public class DiscordReportMCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if (!(sender.hasPermission("alterego.command.discordreport"))) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
			return true;
		}

		if (args.length <= 1 || args[0].equalsIgnoreCase(sender.getName())) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.Usage")
					.replaceAll("%player%", args[0])
					.replaceAll("\\n", "\n")));
			return true;
		}

		if (Bukkit.getPlayer(args[0]) == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.Offline")
					.replaceAll("%player%", args[0])
					.replaceAll("\\n", "\n")));
			return true;
		}

		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.Success")
				.replaceAll("%player%", args[0])
				.replaceAll("\\n", "\n")));

		String message = sender.getName() + " has reported user " + args[0] + " for the following reason: " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		AlterEgoPlugin.report(message);

		String messageToReported = ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.IveBeenNaughty")
				.replaceAll("%player%", sender.getName())
				.replaceAll("%reason%", String.join(" ", Arrays.copyOfRange(args, 1, args.length)))
				.replaceAll("\\n", "\n"));
		Bukkit.getPlayer(args[0]).sendMessage(messageToReported);
		return true;
	}

}
