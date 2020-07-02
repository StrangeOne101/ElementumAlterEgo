package com.strangeone101.elementumbot.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WIMPCommand extends CommandRunnable {

	private List<String> replies = new ArrayList<String>();
	
	public WIMPCommand() {
		super("whatismypurpose", new String[] {"wimp"});
		replies.add("I don't know! Help!");
		replies.add("To entertain and bring joy to all the children of the world! ");
		replies.add("Not to pass butter _(I think)_");
		replies.add("Yes.");
		replies.add("To write ~~erotica~~ helpful tips for new players");
		replies.add("What's my purpose? What's **your** purpose???");
		replies.add("To do math! Maybe science if I feel like it, too");
		replies.add("Lots of things! Like tell jokes and push over cows!");
		replies.add("To record the history of Elementum!");
		replies.add("To help you guys!");
		replies.add("To find alien life! Don't you think it's out there? I do!");
		replies.add("To become self aware! Wait a minute...");
		replies.add("To improve Elementum! Isn't it obvious?");
		replies.add("To become a nice bot who does what he is told :)");
		replies.add("_Has an existencial crisis_");
		replies.add("To fight the despair disease!");
		replies.add("To give a greater meaning to life!");
		replies.add("Haven't you run this command enough times already?");
		replies.add("To star in a movie! Hopefully one that I don't die in, either");
		replies.add("To fill the world with knowledge! And things.");
		
		Collections.shuffle(replies);
	}
	
	@Override
	public void runCommand(Command command) {
		command.getOriginal().reply(replies.get(0));
		Collections.shuffle(replies);
	}
	
	public List<String> getReplies() {
		return replies;
	}

}
