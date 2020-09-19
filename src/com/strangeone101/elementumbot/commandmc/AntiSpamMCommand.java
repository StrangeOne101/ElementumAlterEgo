package com.strangeone101.elementumbot.commandmc;

import com.strangeone101.elementumbot.ai.tasks.AntiSpam;
import com.strangeone101.elementumbot.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AntiSpamMCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage is /antispam <enable/disable/stats/reset> [user]");
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            ConfigManager.getConfig().set("AntiSpam.Enabled", false);
            sender.sendMessage(ChatColor.RED + "AlterEgo Antispam disabled.");
            return true;
        } else if (args[0].equalsIgnoreCase("enable")) {
            ConfigManager.getConfig().set("AntiSpam.Enabled", true);
            sender.sendMessage(ChatColor.GREEN + "AlterEgo Antispam enabled.");

            return true;
        } else if (args[0].equalsIgnoreCase("stats")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage is /antispam stats [user]");
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (player == null || !player.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Player has not played before!");
                return true;
            }
            String stats = AntiSpam.getStats(player);
            sender.sendMessage(stats);
            return true;
        } else if (args[0].equalsIgnoreCase("reset")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage is /antispam reset [user]");
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (player == null || !player.hasPlayedBefore()) {
                sender.sendMessage(ChatColor.RED + "Player has not played before!");
                return true;
            }

            if (AntiSpam.reset(player.getUniqueId())) {
                sender.sendMessage(ChatColor.GREEN + "Reset spam logs for player " + player.getName());
                return true;
            }
            sender.sendMessage(ChatColor.RED + "No spam lots to clear for player " + player.getName());
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Usage is /antispam <enable/disable/stats/reset> [user]");
        return true;
    }
}
