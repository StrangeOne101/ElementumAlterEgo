package com.strangeone101.elementumbot.command;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.util.Reactions;
import com.strangeone101.elementumbot.util.StringUtil;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;

public class SayCommand extends CommandRunnable {

	public SayCommand() {
		super("say");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		
		if (command.getArguments().length < 2) {
			command.reply("Error: Command usage is `!say <channel> <message>`!");
			return;
		}
		
		TextChannel chan = null;
		if (command.getArguments()[0].startsWith("<#")) { //If it's a mentioned channel (e.g. #general), it is in the format of <#XXXXXXXXXXXXXXX>
			String channelID = command.getArguments()[0].substring(2, command.getArguments()[0].length() - 1); 
			
			if (AlterEgoPlugin.API.getTextChannelById(channelID).isPresent()) {
				chan = AlterEgoPlugin.API.getTextChannelById(channelID).get();
			} else {
				command.reply("Error: Channel not found!");
				return;
			}
		} else {
			for (TextChannel channel : AlterEgoPlugin.SERVER.getTextChannels()) {
				if (channel.asServerChannel().get().getName().equalsIgnoreCase(command.getArguments()[0])) {
					chan = channel;
					break;
				}
			}
			
			if (chan == null) { //Still no channel found
				command.reply("Error: Channel not found!");
				return;
			}
		}
		
		String message = StringUtil.combine(command.getArguments());
		message = message.substring(command.getArguments()[0].length() + 1); //Remove the channel from being sent too
		message = message.trim();
		
		chan.sendMessage(message);
		command.getOriginal().addReaction(Reactions.GREEN_TICK + "");
	}

}
