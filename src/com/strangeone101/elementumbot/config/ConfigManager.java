package com.strangeone101.elementumbot.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

	public static ConfigClass defaultConfig;
	
	private static final String _defaultToken = "***insert token here***";
	private static final String _defaultRelayChannelID = "0000000000000000000";

	public ConfigManager() {
		defaultConfig = new ConfigClass("config.yml");
		
		FileConfiguration config = defaultConfig.get();
		
		config.addDefault("Token", _defaultToken);
		config.addDefault("Ops", Arrays.asList(new String[] {"145436402107154433"})); //Default list containing Strange's ID
		config.addDefault("BarredUsers", new ArrayList<String>());
		config.addDefault("RelayChannelID", _defaultRelayChannelID);
		config.addDefault("RelayEnabled", true);
		config.options().copyDefaults(true);
		save();
	}

	public static FileConfiguration getConfig() {
		return ConfigManager.defaultConfig.get();
	}
	

	public static void save() {
		defaultConfig.saveConfig();
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
	
	public static boolean isValid() {
		return !getToken().equalsIgnoreCase(_defaultToken);
	}
	
	public static boolean isValidRelayChannel() {
		return !getRelayChannel().equalsIgnoreCase(_defaultRelayChannelID);
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

}
