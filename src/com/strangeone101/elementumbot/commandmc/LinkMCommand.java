package com.strangeone101.elementumbot.commandmc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.command.LinkCommand;

public class LinkMCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can run this command!");
			return true;
		}
		if (!sender.hasPermission("alterego.command.link")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
			return true;
		}
		if (LinkCommand.links.containsValue(((Player)sender).getUniqueId()) && args.length == 0) {
			sender.sendMessage(AlterEgoPlugin.PREFIX + ChatColor.RED + " You already have an account linked! Use " + ChatColor.YELLOW + "/unlink" + ChatColor.RED + " to unlink.");
			return true;
		}
		if (args.length == 0 || !(args[0].startsWith("#") || args[0].startsWith("!"))) {
			sender.sendMessage(AlterEgoPlugin.PREFIX + ChatColor.GREEN + " Use the " + ChatColor.YELLOW + "!link " + sender.getName() + ChatColor.GREEN + " from discord to link!");
			return true;
		}
		
		if (args[0].startsWith("#")) {
			try {
				int i = Integer.parseInt(args[0].substring(1));
				if (LinkCommand.linksBeingPrepared.containsKey(i)) {
					LinkCommand.finalizeLink(i, (Player) sender);
					sender.sendMessage(ChatColor.GREEN + "Your account has been successfully linked!");
					//TODO make it test LuckPerms
				} else {
					sender.sendMessage(ChatColor.RED + "Operation has already been executed or has expired!");
				}
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.GREEN + "Use the " + ChatColor.YELLOW + "!link " + sender.getName() + ChatColor.GREEN + " from discord to link!");
			}
		} else if (args[0].startsWith("!")) {
			try {
				int i = Integer.parseInt(args[0].substring(1));
				if (LinkCommand.linksBeingPrepared.containsKey(i)) {
					LinkCommand.reportLink(i, (Player) sender);
					sender.sendMessage(ChatColor.RED + "Thanks. This has been reported.");
				} else {
					sender.sendMessage(ChatColor.RED + "Operation has already been executed or has expired!");
				}
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.GREEN + "Use the " + ChatColor.YELLOW + "!link " + sender.getName() + ChatColor.GREEN + " from discord to link!");
			}
		}
		return true;
	}
}
