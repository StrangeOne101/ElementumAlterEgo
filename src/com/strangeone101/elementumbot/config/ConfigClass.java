package com.strangeone101.elementumbot.config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.strangeone101.elementumbot.AlterEgoPlugin;

public class ConfigClass {
	AlterEgoPlugin plugin;
	public static File pluginFolder;
	private File file;
	private FileConfiguration config;

	public ConfigClass(String fileName) {
		this.plugin = AlterEgoPlugin.INSTANCE;
		if (!plugin.getDataFolder().exists()) {
			try {
				plugin.getDataFolder().mkdir();
			} catch (Exception e) {
				plugin.getLogger().info("Failed to generate plugin data folder!");
				e.printStackTrace();
			}
		}

		this.file = new File(plugin.getDataFolder() + File.separator + fileName);
		reloadConfig();
	}

	public FileConfiguration get() {
		return config;
	}
	
	public File getFile() {
		return file;
	}
	
	public void reloadConfig() {
		createConfig();
		this.config = YamlConfiguration.loadConfiguration(this.file);
		loadConfig();
		saveConfig();
	}

	public void createConfig() {
		if (file.exists()) {
			return;
		}

		try {
			file.createNewFile();
		} catch (Exception e) {
			this.plugin.getLogger().info("Failed to generate config file!");
			e.printStackTrace();
		}
	}

	public void loadConfig() {
		try {
			config.load(file);
		} catch (Exception e) {
			this.plugin.getLogger().info("Failed to load config file!");
			e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			config.save(file);
		} catch (Exception e) {
			this.plugin.getLogger().info("Failed to save config file!");
			e.printStackTrace();
		}
	}
}