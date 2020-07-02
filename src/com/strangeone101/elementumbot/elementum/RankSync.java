package com.strangeone101.elementumbot.elementum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.command.LinkCommand;

import com.strangeone101.elementumbot.config.ConfigManager;
import de.btobastian.javacord.entities.permissions.Role;
import joptsimple.internal.Strings;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedPermissionData;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;


public class RankSync {
	
	//public static List<Group> donors;
	public static Group donorBase;
	public static Role donorRole;
	
	public static Group waterChief;
	public static Group earthKing;
	public static Group fireLord;
	public static Group airElder;
	public static Group equalist;
	public static Group avatar;
	
	public static Role waterChiefRole;
	public static Role earthKingRole;
	public static Role fireLordRole;
	public static Role airElderRole;
	public static Role equalistRole;
	public static Role avatarRole;
	
	public static Map<Group, Role> groupsToRoles = new HashMap<Group, Role>();
	
	public static List<Role> allRoles = new ArrayList<Role>();
	
	static {



		/*donors = new ArrayList<Group>();
		
		for (String name : new String[] {"jester", "knight", "priest", "regent", "prince", "princess", "emperor", "defender"}) {
			if (LuckPerms.getApi().getGroup(name) != null) {
				donors.add(LuckPerms.getApi().getGroup(name));
			}
		}*/
		
		/*donorBase = LuckPermsProvider.get().getGroupManager().getGroup("jester");
		
		waterChief = LuckPermsProvider.get().getGroupManager().getGroup("waterchief");
		earthKing = LuckPermsProvider.get().getGroupManager().getGroup("earthking");
		fireLord = LuckPermsProvider.get().getGroupManager().getGroup("firelord");
		airElder = LuckPermsProvider.get().getGroupManager().getGroup("airelder");
		equalist = LuckPermsProvider.get().getGroupManager().getGroup("equalist");
		avatar = LuckPermsProvider.get().getGroupManager().getGroup("avatar");*/
		
		
		if (AlterEgoPlugin.SERVER != null) {
			/*donorRole = AlterEgoPlugin.SERVER.getRoleById("365580673404370944");
			waterChiefRole = AlterEgoPlugin.SERVER.getRoleById("413224304231317505");
			earthKingRole = AlterEgoPlugin.SERVER.getRoleById("413224510054203392");
			fireLordRole = AlterEgoPlugin.SERVER.getRoleById("329362911166464000");
			airElderRole = AlterEgoPlugin.SERVER.getRoleById("413224821208383488");
			equalistRole = AlterEgoPlugin.SERVER.getRoleById("413225081561677834");
			avatarRole = AlterEgoPlugin.SERVER.getRoleById("413225159680589824");
			
			
			groupsToRoles.put(waterChief, waterChiefRole);
			groupsToRoles.put(earthKing, earthKingRole);
			groupsToRoles.put(fireLord, fireLordRole);
			groupsToRoles.put(airElder, airElderRole);
			groupsToRoles.put(equalist, equalistRole);
			groupsToRoles.put(avatar, avatarRole);
			
			allRoles.add(donorRole);
			for (Role r : groupsToRoles.values()) {
				allRoles.add(r);
			}*/
			for (String key : ConfigManager.defaultConfig.get().getConfigurationSection("RankSync").getKeys(false)) {
				for (String id : ConfigManager.defaultConfig.get().getStringList("RankSync." + key)) {
					Role role = AlterEgoPlugin.SERVER.getRoleById(id);

					if (role != null && !allRoles.contains(role))
						allRoles.add(role);
				}
			}

			//AlterEgoPlugin.INSTANCE.getLogger().info("DEBUG - ALL ROLES FOUND: " + Strings.join(allRoles.stream().map(role -> role.getName()).collect(Collectors.toList()), " "));
		}
	}
	
	public static void syncRank(de.btobastian.javacord.entities.User user) {
		if (AlterEgoPlugin.SERVER == null) return;
		if (LinkCommand.isLinked(user.getId())) {
			syncRank(LuckPermsProvider.get().getUserManager().getUser(LinkCommand.getUUIDFromID(user.getId())));
		}
	}
	
	public static void syncRank(User user) {
		if (AlterEgoPlugin.SERVER == null) return;
		
		if (LinkCommand.isLinked(user.getUniqueId())) {

			try {
				de.btobastian.javacord.entities.User discordUser = AlterEgoPlugin.API.getUserById(LinkCommand.links.get(user.getUniqueId())).get();

				List<String> groups = getPermissionGroups(user); //Gets all roles this luckperms user has
				List<Role> roles = getDiscordRoles(groups); //Get the discord equivilant of the groups.

				List<Role> removeRoles = new ArrayList<>(allRoles);
				removeRoles.removeAll(roles);

				//AlterEgoPlugin.INSTANCE.getLogger().info("DEBUG - ROLES TO REMOVE: " + Strings.join(removeRoles.stream().map(role -> role.getName()).collect(Collectors.toList()), " "));
				//AlterEgoPlugin.INSTANCE.getLogger().info("DEBUG - ROLES TO ADD: " + Strings.join(roles.stream().map(role -> role.getName()).collect(Collectors.toList()), " "));

				removeRoles.forEach(r -> r.removeUser(discordUser));
				roles.forEach(r -> r.addUser(discordUser));



			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean isInGroup(User user, Group group) {
		return isInGroup(user, group.getName());
	}

	private static boolean isInGroup(User user, String group) {
		ContextManager cm = LuckPermsProvider.get().getContextManager();
		QueryOptions queryOptions = cm.getQueryOptions(user).orElse(cm.getStaticQueryOptions());
		CachedPermissionData permissionData = user.getCachedData().getPermissionData(queryOptions);

		return permissionData.checkPermission("group." + group).asBoolean();
	}

	private static List<String> getPermissionGroups(User user) {
		List<String> groups = new ArrayList<>();

		for (String mcGroup : ConfigManager.defaultConfig.get().getConfigurationSection("RankSync").getKeys(false)) {
			if (isInGroup(user, mcGroup)) {
				groups.add(mcGroup);
			}
		}
		return groups;
	}

	public static List<Role> getDiscordRoles(List<String> groups) {
		List<Role> roles = new ArrayList<>();
		for (String key : groups) {
			for (String id : ConfigManager.defaultConfig.get().getStringList("RankSync." + key)) {
				Role role = AlterEgoPlugin.SERVER.getRoleById(id);
				if (!roles.contains(role) && role != null) {
					roles.add(role);
				}
			}
		}
		return roles;
	}

}
