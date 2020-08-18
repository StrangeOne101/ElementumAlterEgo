package com.strangeone101.elementumbot.command;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.Essentials;
import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.MessageHandler;
import com.strangeone101.elementumbot.util.CleanupHandler;
import com.strangeone101.elementumbot.util.PluginUtil;
import org.javacord.api.entity.message.Message;

public class ListDCommand extends CommandRunnable {

	public ListDCommand() {
		super("listd");
	}
	
	@Override
	public void runCommand(Command command) {
		String players = "";
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (PluginUtil.isEssentialsEnabled() && ((Essentials)Bukkit.getPluginManager().getPlugin("Essentials")).getUser(p).isVanished()) continue;
			players = players + ", " + MessageHandler.format(p.getDisplayName());
		}
		
		if (players.equals("")) players = "None";
		else players = players.substring(2);
		
		Future<Message> msg = command.reply("Currently online (" + players.split(",").length + "): " + ChatColor.stripColor(players));
		CleanupHandler.cleanup(command.getOriginal(), 1000L * 60 * 5);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				try {
					Message message = msg.get();
					CleanupHandler.cleanup(message, 1000L * 60 * 5);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				
			}
		}.runTask(AlterEgoPlugin.INSTANCE);
	}
}
