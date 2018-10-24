package com.strangeone101.elementumbot.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.help.HelpTopic;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.util.AliasRegistry;
import com.strangeone101.elementumbot.util.Executable;
import com.strangeone101.elementumbot.util.Reactions;

public class AliasCommand extends CommandRunnable{

	public AliasCommand() {
		super("alias");
	}
	
	@Override
	public void runCommand(Command command) {
		if (command.getArguments().length < 1) {
			command.getOriginal().reply("Usage is `!alias <list/create/delete> [alias] [mc cmd] [cmd args...]`");
			return;
		}
		String subcommand = command.getArguments()[0];
		if (subcommand.equalsIgnoreCase("list")) {
			for (String alias : AliasRegistry.getAliasCommands()) {
				Executable exe = AliasRegistry.getExecutable(alias);
				command.getOriginal().reply("- " + alias + " > " + exe.getCommand() + " " + exe.getArgsString());
			}
			return;
		}
		
		if (!command.hasOppedPower()) return;
		
		if (command.getArguments().length < 2) {
			command.getOriginal().reply("You must specify the alias to create or delete!");
			return;
		}
		
		String alias = command.getArguments()[1];
		
		if (subcommand.equalsIgnoreCase("create")) {
			if (command.getArguments().length < 3) {
				command.getOriginal().reply("You must specify the command for the alias!");
				return;
			}
			
			if (Command.commandExists(alias) || AliasRegistry.getAliasCommands().contains(alias)) {
				command.getOriginal().reply("There already exists a command for that alias!");
				return;
			}
			
			String aliasCmd = command.getArguments()[2];
			String[] aliasArgs = new String[command.getArguments().length - 3];
			
			for (int i = 3; i < command.getArguments().length; i++) {
				aliasArgs[i - 3] = command.getArguments()[i];
			}
			
			Set<String> cmds = new HashSet<>();
			for (HelpTopic help : Bukkit.getServer().getHelpMap().getHelpTopics()) {
				String cmd = help.getName();
				String[] split = cmd.split(":");
				
				if (split.length >= 2) {
					switch (split[0].toLowerCase()) {
						case "minecraft":
						case "bukkit":
							cmds.add(split[1]);
							break;
					}
				} else {
					cmds.add(cmd);
				}
			}
			
			cmds.addAll(Bukkit.getCommandAliases().keySet());
			for (String key : Bukkit.getCommandAliases().keySet()) {
				cmds.addAll(Arrays.asList(Bukkit.getCommandAliases().get(key)));
			}
			
			if (!cmds.contains("/" + aliasCmd.toLowerCase()) && Bukkit.getPluginCommand(aliasCmd.toLowerCase()) == null) {
				command.getOriginal().reply("That isn't a known command!");
				AlterEgoPlugin.INSTANCE.getLogger().info(cmds.toString());
				return;
			}
			
			AliasRegistry.addNewAliasCommand(alias, aliasCmd, aliasArgs);
			command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
		} else if (subcommand.equalsIgnoreCase("delete")) {
			AliasRegistry.deleteAliasCommand(alias);
			command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
		}
	}

}
