package com.strangeone101.elementumbot.command;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.CleanupHandler;
import com.strangeone101.elementumbot.MessageHandler;

import de.btobastian.javacord.entities.message.Message;

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
		Future<Message> msg = command.getOriginal().reply("Currently online: " + players);
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
