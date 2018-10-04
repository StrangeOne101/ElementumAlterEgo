package com.strangeone101.elementumbot.command;

import java.util.HashMap;

import com.strangeone101.elementumbot.config.ConfigManager;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;

public class Command {
	
	private static HashMap<String, CommandRunnable> commands = new HashMap<String, CommandRunnable>();
	
	private String command;
	private String[] args;
	private User sender;
	private Message original;
	private boolean op;
	
	public Command(Message message) {
		this.command = message.getContent().split(" ")[0].toLowerCase();
		if (command.startsWith("!")) command = command.substring(1);
		
		this.args = new String[0];
		
		if (message.getContent().contains(" ")) {
			args = message.getContent().substring(command.length() + 2).split(" ");
		}
		
		this.sender = message.getAuthor();
		this.original = message;
		this.op = ConfigManager.getOps().contains(sender.getId());
		
		if (commands.containsKey(command)) {
			commands.get(command).runCommand(this);
		} else {
			if (ConfigManager.getOpAliases().containsKey(command) && this.op) {
				this.command = ConfigManager.getOpAliases().get(command);
				//this.original.reply(command + " is cmd");
				if (this.command.contains(" ")) {
					this.args = recreateArgs(this.args, this.command);
					this.command = this.command.split(" ")[0];
				}
				if (commands.containsKey(command)) {
					commands.get(command).runCommand(this);
					return;
				}
			} 
			if (ConfigManager.getAliases().containsKey(command)) {
				this.command = ConfigManager.getAliases().get(command);
				if (this.command.contains(" ")) {
					this.args = recreateArgs(this.args, this.command);
					this.command = this.command.split(" ")[0];
				}
				if (commands.containsKey(command)) {
					commands.get(command).runCommand(this);
					return;
				}
			}
		}
	}
	
	private String[] recreateArgs(String[] original, String alias) {
		String[] newArgs = new String[original.length + alias.split(" ").length - 1];
		//this.original.reply(newArgs.length + " | " + original.length + " | " + (alias.split(" ").length - 1));
		int i;
		for (i = 1; i < alias.split(" ").length; i++) {
			newArgs[i - 1] = alias.split(" ")[i];
			//this.original.reply(alias.split(" ")[i] + " | " + (i - 1));
		}
		for (int j = 0; j < original.length; j++) {
			newArgs[i - 1 + j] = original[j];
			//this.original.reply(original[j] + " | " + (i - 1 + j));
		}
		return newArgs;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String[] getArguments() {
		return args;
	}

	public Message getOriginal() {
		return original;
	}
	
	public User getSender() {
		return sender;
	}
	
	public boolean hasOppedPower() {
		return op;
	}
	
	public static void registerCommand(String command, CommandRunnable executor) {
		commands.put(command.toLowerCase(), executor);
	}
	
	public static void registerDefaultCommands() {
		new BarCommand();
		new UnbarCommand();
		new BarListCommand();
		new ListCommand();
		new ListDCommand();
		new HelpCommand();
		new ReloadCommand();
		new ExecuteCommand();
		new RelayCommand();
		new SayCommand();
		new InfoCommand();
		new LinkCommand();
		new UnlinkCommand();
		new DebugCommand();
		new ReturnCommand();
		new WhoIsCommand();
		new LatencyCommand();
	}
}
