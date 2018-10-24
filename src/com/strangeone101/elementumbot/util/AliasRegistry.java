package com.strangeone101.elementumbot.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.strangeone101.elementumbot.command.AliasCommandRunnable;
import com.strangeone101.elementumbot.config.ConfigManager;

public class AliasRegistry {
	
	public static Map<String, Executable> aliases = new HashMap<>();
	
	public AliasRegistry() {
		loadAliasCommands();
	}
	
	public static Set<String> getAliasCommands() {
		return new HashSet<>(aliases.keySet());
	}
	
	public static Executable getExecutable(String alias) {
		if (aliases.containsKey(alias)) {
			return aliases.get(alias);
		} else {
			return null;
		}
	}
	
	public static void addNewAliasCommand(String alias, String cmd, String... args) {
		Executable exe = new Executable(cmd, args);
		aliases.put(alias, exe);
		saveAliasCommands();
		
		new AliasCommandRunnable(alias, exe);
	}
	
	public static void saveAliasCommands() {
		FileConfiguration config = ConfigManager.aliasConfig.get();
		
		for (String alias : aliases.keySet()) {
			Executable exe = aliases.get(alias);
			
			config.set(alias + ".Command", exe.getCommand());
			config.set(alias + ".ArgFormat", exe.getArgsString());
		}
		
		ConfigManager.aliasConfig.saveConfig();
	}
	
	public static void deleteAliasCommand(String alias) {
		if (!aliases.containsKey(alias)) {
			return;
		}
		
		aliases.remove(alias);
		
		ConfigManager.aliasConfig.getFile().delete();
		ConfigManager.aliasConfig.reloadConfig();
		
		saveAliasCommands();
	}
	
	public static void loadAliasCommands() {
		FileConfiguration config = ConfigManager.aliasConfig.get();
		
		for (String key : config.getKeys(false)) {
			String argCmd = config.getString(key + ".Command");
			String argString = config.getString(key + ".ArgFormat");
			Executable exe = new Executable(argCmd, argString.split(" "));
			new AliasCommandRunnable(key, exe);
			aliases.put(key, exe);
		}
	}
}
