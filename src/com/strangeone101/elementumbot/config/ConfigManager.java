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
		config.addDefault("RelayFormat", "**%player%** \u00BB %message%");
		config.addDefault("InGameFormat", "&7[Discord] <name>: &r<message>");
		config.addDefault("Aliases", Arrays.asList(new String[] {"example:list"}));
		config.addDefault("OpAliases", Arrays.asList(new String[] {"opexample:list"}));
		config.addDefault("LinkRewardCommands", Arrays.asList((new String[] {"crate key %player% epic", "tellraw %player% {\"color\":\"yellow\",\"text\":\"You are rewarded 1 Epic Key for linking your account!\"}"})));
		config.addDefault("RankSync.Donor", Arrays.asList((new String[] {"000000000000"})));
		config.addDefault("Report.Usage", "&cCommand usage is /report <user> <reason>");
		config.addDefault("Report.Success", "&aThanks. %player% has been reported and will be dealt with shortly.");
		config.addDefault("Report.Offline", "&cThat player is offline.");
		config.addDefault("Report.IveBeenNaughty", "&cYou have been reported by %player% for the reason: %reason%");
		config.options().copyDefaults(true);
		
		linkConfig = new ConfigClass("links.yml");
		
		FileConfiguration config2 = linkConfig.get();		
		
		
		for (String key : config2.getKeys(false)) {
			LinkCommand.links.put(UUID.fromString(key), Long.valueOf(config2.getString(key)));
		}
		
		returnConfig = new ConfigClass("savedRoles.yml");
		
		//FileConfiguration config3 = returnConfig.get();
		
		save();
	}

	public static FileConfiguration getConfig() {
		return ConfigManager.defaultConfig.get();
	}
	

	public static void save() {
		defaultConfig.saveConfig();
		
		for (UUID id : LinkCommand.links.keySet()) {
			linkConfig.get().set(id.toString(), LinkCommand.links.get(id));
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

	public static String getRelayFormat() {
		return defaultConfig.get().getString("RelayFormat");
	}

	public static List<String> getLinkRewardCommands() {
		return defaultConfig.get().getStringList("LinkRewardCommands");
	}
	
	public static long getRelayChannel() {
		return Long.parseLong(defaultConfig.get().getString("RelayChannelID"));
	}
	
	public static long getReportChannel() {
		return Long.parseLong(defaultConfig.get().getString("ReportChannelID"));
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
		return !(getToken() + "").equalsIgnoreCase(_defaultToken);
	}
	
	public static boolean isValidRelayChannel() {
		return !(getRelayChannel() + "").equalsIgnoreCase(_defaultRelayChannelID);
	}
	
	public static boolean isValidReportChannel() {
		return !(getReportChannel() + "").equalsIgnoreCase(_defaultRelayChannelID);
	}
	
	public static void addBarredUser(long id) {
		List<String> list = getBarredUsers();
		list.remove(id); //In case it was already there - no double ups allowed
		list.add(id + "");
		defaultConfig.get().set("BarredUsers", list);
		save();
	}
	
	public static void addOp(long id) {
		List<String> list = getOps();
		list.remove(id); //In case it was already there - no double ups allowed
		list.add(id + "");
		defaultConfig.get().set("Ops", list);
		save();
	}
	
	public static void removeBarredUser(long id) {
		List<String> list = getBarredUsers();
		list.remove(id + "");
		defaultConfig.get().set("BarredUsers", list);
		save();
	}
	
	public static void removeOp(long id) {
		List<String> list = getOps();
		list.remove(id + "");
		defaultConfig.get().set("Ops", list);
		save();
	}
	
	public static void setRelay(boolean b) {
		defaultConfig.get().set("RelayEnabled", b);
		save();
	}
	
	public static String getSayCommandFormat() {
		return ChatColor.translateAlternateColorCodes('&', defaultConfig.get().getString("InGameFormat"));
	}

}
