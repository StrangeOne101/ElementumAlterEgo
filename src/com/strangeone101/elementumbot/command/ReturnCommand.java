package com.strangeone101.elementumbot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.config.ConfigClass;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Reactions;

import de.btobastian.javacord.entities.permissions.Role;

public class ReturnCommand extends CommandRunnable {
	
	public ReturnCommand() {
		super("return");
	}

	@Override
	public void runCommand(Command command) {
		if (command.getArguments().length > 0) {
			String[] createAliases = {"create", "setup", "update", "backup"};
			if (Arrays.asList(createAliases).contains(command.getArguments()[0].toLowerCase())) {
				if (!command.hasOppedPower()) {
					command.getOriginal().addUnicodeReaction(Reactions.RED_CROSS + "");
					return;
				}
				
				ConfigClass config = ConfigManager.returnConfig;
				List<String> list = new ArrayList<String>();
				for (Role r : command.getSender().getRoles(AlterEgoPlugin.SERVER)) {
					list.add(r.getId());
				}
				String addedUpdated = config.get().contains(command.getSender().getId()) ? "updated" : "added";
				config.get().set(command.getSender().getId(), list);
				config.saveConfig();
				command.getOriginal().reply("Return roles successfully " + addedUpdated + "!");
			} else {
				command.getOriginal().reply("Usage is `!return <setup/update>` to update your return roles!");
			}
		} else {
			ConfigClass config = ConfigManager.returnConfig;
			
			if (config.get().contains(command.getSender().getId())) {
				List<String> roles = config.get().getStringList(command.getSender().getId());					
				boolean b = false;
				for (String role : roles) {
					if (AlterEgoPlugin.SERVER.getRoleById(role) != null && command.getSender().getRoles(AlterEgoPlugin.SERVER).contains(AlterEgoPlugin.SERVER.getRoleById(role))) {
						AlterEgoPlugin.SERVER.getRoleById(role).addUser(command.getSender());
						b = true;
					}
				}
				if (b) {
					command.getOriginal().reply("Your roles have been returned! Welcome back, " + command.getSender().getMentionTag() + "!");
				} else {
					command.getOriginal().reply("Error: There are no roles to update for you currently!");
				}
			} else {
				command.getOriginal().reply("Error: You haven't saved any roles, so there is nothing to revert to!");
			}
		}

	}

}
