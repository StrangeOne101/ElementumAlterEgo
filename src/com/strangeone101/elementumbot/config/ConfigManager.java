package com.strangeone101.elementumbot.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.strangeone101.elementumbot.command.LinkCommand;

import net.md_5.bungee.api.ChatColor;

public class ConfigManager {

	public static ConfigClass defaultConfig;
	public static ConfigClass linkConfig;
	public static ConfigClass returnConfig;
	public static ConfigClass aliasConfig;
	
	private static final String _defaultToken = "***insert token here***";
	private static final String _defaultRelayChannelID = "0000000000000000000";

	public ConfigManager() {
		defaultConfig = new ConfigClass("config.yml");
		
		FileConfiguration config = defaultConfig.get();
		
		config.addDefault("Token", _defaultToken);
		config.addDefault("Ops", Arrays.asList(new String[] {"145436402107154433"})); //Default list containing Strange's ID
		config.addDefault("BarredUsers", new ArrayList<String>());
		config.addDefault("RelayChannelID", _defaultRelayChannelID);
		config.addDefault("ReportChannelID", _defaultRelayChannelID);
		config.addDefault("RelayEnabled", true);
		config.addDefault("SayCommandFormat", "&7[Discord] <name>: &r<message>");
		config.addDefault("Aliases", Arrays.asList(new String[] {"example:list"}));
		config.addDefault("OpAliases", Arrays.asList(new String[] {"opexample:list"}));
		config.options().copyDefaults(true);
		
		linkConfig = new ConfigClass("links.yml");
		
		FileConfiguration config2 = linkConfig.get();		
		
		
		for (String key : config2.getKeys(false)) {
			LinkCommand.links.put(key, UUID.fromString(config2.getString(key)));
		}
		
		returnConfig = new ConfigClass("savedRoles.yml");
		
		//FileConfiguration config3 = returnConfig.get();
		
		aliasConfig = new ConfigClass("aliasCommands.yml"); //Nothing needs to be in there by default, just needs to exist
		save();
	}

	public static FileConfiguration getConfig() {
		return ConfigManager.defaultConfig.get();
	}
	

	public static void save() {
		defaultConfig.saveConfig();
		
		for (String id : LinkCommand.links.keySet()) {
			linkConfig.get().set(id, LinkCommand.links.get(id).toString());
		}
		
		linkConfig.saveConfig();
	}
	
	public static List<String> getOps() {
		return defaultConfig.get().getStringList("Ops");
	}
	
	public static List<String> getBarredUsers() {
		return defaultConfig.get().getStringList("BarredUsers");
	}
	
	public static boolean getRelay() {
		return defaultConfig.get().getBoolean("RelayEnabled");
	}
	
	public static String getToken() {
		return defaultConfig.get().getString("Token");
	}
	
	public static String getRelayChannel() {
		return defaultConfig.get().getString("RelayChannelID");
	}
	
	public static String getReportChannel() {
		return defaultConfig.get().getString("ReportChannelID");
	}
	
	public static Map<String, String> getAliases() {
		Map<String, String> aliases = new HashMap<String, String>();
		for (String s : defaultConfig.get().getStringList("Aliases")) {
			aliases.put(s.split("\\:", 2)[0], s.split("\\:", 2)[1]);
		}
		return aliases;
	}
	
	public static Map<String, String> getOpAliases() {
		Map<String, String> aliases = new HashMap<String, String>();
		for (String s : defaultConfig.get().getStringList("OpAliases")) {
			aliases.put(s.split("\\:", 2)[0], s.split("\\:", 2)[1]);
		}
		return aliases;
	}
	
	public static boolean isValid() {
		return !getToken().equalsIgnoreCase(_defaultToken);
	}
	
	public static boolean isValidRelayChannel() {
		return !getRelayChannel().equalsIgnoreCase(_defaultRelayChannelID);
	}
	
	public static boolean isValidReportChannel() {
		return !getReportChannel().equalsIgnoreCase(_defaultRelayChannelID);
	}
	
	public static void addBarredUser(String id) {
		List<String> list = getBarredUsers();
		list.remove(id); //In case it was already there - no double ups allowed
		list.add(id);
		defaultConfig.get().set("BarredUsers", list);
		save();
	}
	
	public static void addOp(String id) {
		List<String> list = getOps();
		list.remove(id); //In case it was already there - no double ups allowed
		list.add(id);
		defaultConfig.get().set("Ops", list);
		save();
	}
	
	public static void removeBarredUser(String id) {
		List<String> list = getBarredUsers();
		list.remove(id);
		defaultConfig.get().set("BarredUsers", list);
		save();
	}
	
	public static void removeOp(String id) {
		List<String> list = getOps();
		list.remove(id);
		defaultConfig.get().set("Ops", list);
		save();
	}
	
	public static void setRelay(boolean b) {
		defaultConfig.get().set("RelayEnabled", b);
		save();
	}
	
	public static String getSayCommandFormat() {
		return ChatColor.translateAlternateColorCodes('&', defaultConfig.get().getString("SayCommandFormat"));
	}

}
