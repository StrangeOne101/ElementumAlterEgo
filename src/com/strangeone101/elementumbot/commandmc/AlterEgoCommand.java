package com.strangeone101.elementumbot.commandmc;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.chatbot.BrainMuxer;
import com.strangeone101.elementumbot.chatbot.LearningChatbot;

public class AlterEgoCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if (!sender.hasPermission("alterego.command.alterego")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(usage());
			return true;
		}
		if (args[0].equalsIgnoreCase("chatbot")) {
			if (args.length == 1) {
				sender.sendMessage(usage());
				return true;
			}
			
			if (args[1].equalsIgnoreCase("stats")) {
				String s = ChatColor.translateAlternateColorCodes('&', 
						"&c-- &aAlter Ego Chatbot Statistics &c--\n" 
					+	"Word Count: " + LearningChatbot.INSTANCE.getBrain().getWordCount() + "\n"
					+   "Brain File Size: " + LearningChatbot.INSTANCE.getBrainFileSize() + "\n"
					+ 	"Last Sentence Calc Time: " + LearningChatbot.INSTANCE.getBrain().getLastSentenceConstruction() + "ms");
				sender.sendMessage(s);
			} else if (args[1].equalsIgnoreCase("load")) {
				
			} else if (args[1].equalsIgnoreCase("save")) {
				new BukkitRunnable() {

					@Override
					public void run() {
						sender.sendMessage(ChatColor.GREEN + "Saving brain...");
						long time1 = System.currentTimeMillis();
						BrainMuxer.saveBrain(LearningChatbot.INSTANCE.getBrain(), new File(AlterEgoPlugin.INSTANCE.getDataFolder(), "brain.dat"));
						sender.sendMessage(ChatColor.GREEN + "Brain saved (Took " + (System.currentTimeMillis() - time1) + "ms).");
					}
					
				}.runTask(AlterEgoPlugin.INSTANCE);
				
			}
		}
		
		return true;
	}
	
	public String usage() {
		return ChatColor.translateAlternateColorCodes('&', 
				"&c-- &aAlter Ego Configuration &c--\n" +
				"- &a/ae chatbot <stats/load/save>");
	}
	
}
