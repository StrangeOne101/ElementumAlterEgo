package com.strangeone101.elementumbot.command;

public class HelpCommand extends CommandRunnable {

	public HelpCommand() {
		super("help");
	}
	
	@Override
	public void runCommand(Command command) {
		String reply = "Avaliable commands: \n"
				+ "- `!list` - List all online users\n"
				+ "- `!listd` - List all online users (with display names)\n"
				+ "- `!link <user>` - Link your discord user to your MC account\n"
				+ "- `!unlink` - Removes the link to your MC account"
				+ "- `!whois <@user>` - Returns the in game name of a discord user\n";
		command.getOriginal().reply(reply);
		
		if (command.hasOppedPower()) {
			String oppedReply = "Op Commands: \n- `!bar @user` - Bars a user from running commands\n" +
					"- `!unbar @user` - Unbars a user\n" +
					"- `!barlist` - List all barred users\n" +
					"- `!reload` - Reloads the bot config\n" +
					"- `!relay <on/off>` - Disable or enable the relay\n" +
					"- `!return [setup/update]` - Allows you to backup your roles\n" +
					"- `!say <message>` - Send a message in game\n" +
					"- `!info` - Shows the bot version with a link to the source code";
			command.getOriginal().reply(oppedReply);
		}
	}

}
