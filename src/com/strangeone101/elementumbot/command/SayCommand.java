package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.util.Reactions;
import com.strangeone101.elementumbot.util.StringUtil;

import de.btobastian.javacord.entities.Channel;

public class SayCommand extends CommandRunnable {

	public SayCommand() {
		super("say");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		
		if (command.getArguments().length < 2) {
			command.getOriginal().reply("Error: Command usage is `!say <channel> <message>`!");
			return;
		}
		
		Channel chan = null;
		if (command.getArguments()[0].startsWith("<#")) { //If it's a mentioned channel (e.g. #general), it is in the format of <#XXXXXXXXXXXXXXX>
			String channelID = command.getArguments()[0].substring(2, command.getArguments()[0].length() - 1); 
			
			if (AlterEgoPlugin.API.getChannelById(channelID) != null) {
				chan = AlterEgoPlugin.API.getChannelById(channelID);
			} else {
				command.getOriginal().reply("Error: Channel not found!");
				return;
			}
		} else {
			for (Channel channel : AlterEgoPlugin.SERVER.getChannels()) {
				if (channel.getName().equalsIgnoreCase(command.getArguments()[0])) {
					chan = channel;
					break;
				}
			}
			
			if (chan == null) { //Still no channel found
				command.getOriginal().reply("Error: Channel not found!");
				return;
			}
		}
		
		String message = StringUtil.combine(command.getArguments());
		message = message.substring(command.getArguments()[0].length() + 1); //Remove the channel from being sent too
		message = message.trim();
		
		chan.sendMessage(message);
		command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
	}

}
