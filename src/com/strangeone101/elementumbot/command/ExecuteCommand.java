package com.strangeone101.elementumbot.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.FakeCommandSender;
import com.strangeone101.elementumbot.util.Reactions;
import com.strangeone101.elementumbot.util.StringUtil;

public class ExecuteCommand extends CommandRunnable {

	private static final String[] users = new String[] {"145436402107154433", "144641508933369856"}; //Strange, Loony
	
	public ExecuteCommand() {
		super("execute", new String[] {"x"});
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower() && !command.isAlias()) return;
		else if (!Arrays.asList(users).contains(command.getSender().getId() + "") && !command.isAlias()) {
			command.getOriginal().addReaction(Reactions.RED_CROSS + "");
		} else { //Verified as hardcoded user
			if (command.getArguments().length == 0) {
				command.reply("Usage is `!execute <command>`");
				return;
			}
			FakeCommandSender sender = new FakeCommandSender(command.getSender().asUser().get(), command.getOriginal());
			
			String message = StringUtil.combine(command.getArguments());
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
