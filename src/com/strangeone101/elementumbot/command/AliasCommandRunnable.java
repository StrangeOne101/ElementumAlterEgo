package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Executable;
import com.strangeone101.elementumbot.util.Reactions;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageBuilder;

public class AliasCommandRunnable extends CommandRunnable{
	
	private Executable exe;
	
	public AliasCommandRunnable(String alias, Executable exe) {
		super(alias);
		this.exe = exe;
	}
	
	@Override
	public void runCommand(Command command) {
		if (command.getArguments().length > exe.getNumberOfPossibleArgs()) {
			command.getOriginal().reply("Usage is `!" + command.getCommand() + exe.getOptionalArgsString() + "`");
			return;
		}
		
		MessageBuilder mBuilder = new MessageBuilder().append(exe.getExecuteStatement(command.getArguments()));
		Message message = null;
		
		try {
			message = AlterEgoPlugin.API.getChannelById(ConfigManager.getRelayChannel()).sendMessage(mBuilder.build()).get();
		} catch (Exception e) {
			command.getOriginal().reply("Something went wrong while trying to execute the command!");
			return;
		}
		
		message.delete();
		command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
	}

}
