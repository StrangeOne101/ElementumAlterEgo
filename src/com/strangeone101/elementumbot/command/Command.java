package com.strangeone101.elementumbot.command;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import com.strangeone101.elementumbot.config.ConfigManager;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;

public class Command {
	
	private static HashMap<String, CommandRunnable> commands = new HashMap<String, CommandRunnable>();
	
	private String command;
	private String[] args;
	private MessageAuthor sender;
	private Message original;
	private boolean op;
	private boolean isAlias;
	
	public Command(Message message) {
		this.command = message.getContent().split(" ")[0].toLowerCase();
		if (command.startsWith("!")) command = command.substring(1);
		
		this.args = new String[0];
		
		if (message.getContent().contains(" ")) {
			args = message.getContent().substring(command.length() + 2).split(" ");
		}
		
		this.sender = message.getAuthor();
		this.original = message;
		this.op = ConfigManager.getOps().contains(sender.getId() + "");
		
		if (commands.containsKey(command)) {
			commands.get(command).runCommand(this);
		} else {
			if (ConfigManager.getOpAliases().containsKey(command) && this.op) {
				this.command = ConfigManager.getOpAliases().get(command);
				this.isAlias = true;
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
				this.isAlias = true;
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

	public MessageAuthor getSender() {
		return sender;
	}
	
	public boolean hasOppedPower() {
		return op;
	}

	public boolean isAlias() {
		return isAlias;
	}

	public CompletableFuture<Message> reply(String message) {
		return original.getChannel().sendMessage(message);
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
		new WIMPCommand();
		new QOTDCommand();
	}
}
