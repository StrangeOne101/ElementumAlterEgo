package com.strangeone101.elementumbot.util;

import org.bukkit.Bukkit;

public class PluginUtil {
	
	public static boolean isEssentialsEnabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Essentials");
	}

}
