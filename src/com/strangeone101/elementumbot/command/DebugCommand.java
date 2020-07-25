package com.strangeone101.elementumbot.command;

public class DebugCommand extends CommandRunnable {

	public DebugCommand() {
		super("debug");
	}
	
	@Override
	public void runCommand(Command command) {
		if (!command.hasOppedPower()) return;
		
		/*Role role = RankSync.donorRole;
		
		command.reply("Rank 0: " + RankSync.donors[0]);
		command.reply("Rank 0 fname: " + RankSync.donors[0].getFriendlyName());
		command.reply("Rank 0 name: " + RankSync.donors[0].getName());*/
		
	}

}
