package com.strangeone101.elementumbot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.config.ConfigClass;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.Reactions;
import org.javacord.api.entity.permission.Role;

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
					command.getOriginal().addReaction(Reactions.RED_CROSS + "");
					return;
				}
				
				ConfigClass config = ConfigManager.returnConfig;
				List<Long> list = new ArrayList<Long>();
				for (Role r : command.getSender().asUser().get().getRoles(AlterEgoPlugin.SERVER)) {
					list.add(r.getId());
				}
				String addedUpdated = config.get().contains(command.getSender().getId() + "") ? "updated" : "added";
				config.get().set(command.getSender().getId() + "", list);
				config.saveConfig();
				command.reply("Return roles successfully " + addedUpdated + "!");
			} else {
				command.reply("Usage is `!return <setup/update>` to update your return roles!");
			}
		} else {
			ConfigClass config = ConfigManager.returnConfig;
			
			if (config.get().contains(command.getSender().getId() + "")) {
				List<String> roles = config.get().getStringList(command.getSender().getId() + "");
				boolean b = false;
				for (String role : roles) {
					if (AlterEgoPlugin.SERVER.getRoleById(role) != null && command.getSender().asUser().get().getRoles(AlterEgoPlugin.SERVER).contains(AlterEgoPlugin.SERVER.getRoleById(role))) {
						AlterEgoPlugin.SERVER.getRoleById(role).get().addUser(command.getSender().asUser().get());
						b = true;
					}
				}
				if (b) {
					command.reply("Your roles have been returned! Welcome back, " + command.getSender().asUser().get().getMentionTag() + "!");
				} else {
					command.reply("Error: There are no roles to update for you currently!");
				}
			} else {
				command.reply("Error: You haven't saved any roles, so there is nothing to revert to!");
			}
		}

	}

}
