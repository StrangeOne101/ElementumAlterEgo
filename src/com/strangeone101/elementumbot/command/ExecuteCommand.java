package com.strangeone101.elementumbot.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.FakeCommandSender;
import com.strangeone101.elementumbot.util.Reactions;

public class ExecuteCommand extends CommandRunnable {

	private static final String[] users = new String[] {"145436402107154433", "144641508933369856"}; //Strange, Loony
	
	public ExecuteCommand() {
		super("execute");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		else if (!Arrays.asList(users).contains(command.getSender().getId())) {
			command.getOriginal().addUnicodeReaction(Reactions.RED_CROSS + "");
		} else { //Verified as hardcoded user
			if (command.getArguments().length == 0) {
				command.getOriginal().reply("Usage is `!execute <command>`");
				return;
			}
			FakeCommandSender sender = new FakeCommandSender(command.getSender(), command.getOriginal());
			
			String message = command.getOriginal().getContent().substring("!execute ".length());
			message = message.startsWith("/") ? message.substring(1) : message;
			message = message.trim();
			
			final String finalMsg = message;
			
			new BukkitRunnable() {

				@Override
				public void run() {
					Bukkit.dispatchCommand(sender, finalMsg);
				}
				
			}.runTask(AlterEgoPlugin.INSTANCE);
			
			
		}

	}

}
