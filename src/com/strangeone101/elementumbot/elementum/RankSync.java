package com.strangeone101.elementumbot.elementum;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.command.LinkCommand;

import de.btobastian.javacord.entities.permissions.Role;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.User;

public class RankSync {
	
	public static Group[] donors;
	public static Role donorRole;
	
	static {
		donors = new Group[] {
				LuckPerms.getApi().getGroup("jester"),
				LuckPerms.getApi().getGroup("knight"),
				LuckPerms.getApi().getGroup("priest"),
				LuckPerms.getApi().getGroup("regent"),
				LuckPerms.getApi().getGroup("prince"),
				LuckPerms.getApi().getGroup("emperor")
		};
		
		if (AlterEgoPlugin.SERVER != null) {
			donorRole = AlterEgoPlugin.SERVER.getRoleById("365580673404370944");
		}
	}
	
	public static void syncRank(de.btobastian.javacord.entities.User user) {
		if (AlterEgoPlugin.SERVER == null) return;
		if (LinkCommand.links.containsKey(user.getId())) {
			User lpuser = LuckPerms.getApi().getUser(LinkCommand.links.get(user.getId()));
			boolean donor = false;
			for (Group g : donors) {
				if (lpuser.isInGroup(g)) {
					donor = true;
					break;
				}
			}
			
			if (donor && !user.getRoles(AlterEgoPlugin.SERVER).contains(donorRole)) {
				Role[] newRoles = new Role[user.getRoles(AlterEgoPlugin.SERVER).size() + 1];
				for (int i = 0; i < user.getRoles(AlterEgoPlugin.SERVER).size(); i++) {
					newRoles[i] = (Role) user.getRoles(AlterEgoPlugin.SERVER).toArray()[i];
				}
				newRoles[user.getRoles(AlterEgoPlugin.SERVER).size()] = donorRole;
				AlterEgoPlugin.SERVER.updateRoles(user, newRoles);
			}
		}
	}

}
