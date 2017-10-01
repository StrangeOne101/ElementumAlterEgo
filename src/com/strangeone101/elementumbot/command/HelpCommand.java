package com.strangeone101.elementumbot.command;

public class HelpCommand extends CommandRunnable {

	public HelpCommand() {
		super("help");
	}
	
	@Override
	public void runCommand(Command command) {
		String reply = "Avaliable commands: \n- `!list` - List all online users\n- `!listd` - List all online users (with display names)";
		command.getOriginal().reply(reply);
		
		if (command.hasOppedPower()) {
			String oppedReply = "Op Commands: \n- `!bar @user` - Bars a user from running commands\n" +
					"- `!unbar @user` - Unbars a user\n" +
					"- `!barlist` - List all barred users\n" +
					"- `!reload` - Reloads the bot config\n" +
					"- `!relay <on/off>` - Disable or enable the relay";
			command.getOriginal().reply(oppedReply);
		}
	}

}
