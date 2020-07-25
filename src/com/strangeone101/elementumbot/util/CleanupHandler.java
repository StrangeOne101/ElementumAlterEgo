package com.strangeone101.elementumbot.util;

import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import org.javacord.api.entity.message.Message;

public class CleanupHandler {
	
	/**
	 * Deletes a message after so many milliseconds. Helps reduce spam.
	 * @param message The message
	 * @param time The time 
	 */
	public static void cleanup(Message message, long time) {
		new BukkitRunnable() {
			@Override
			public void run() {
				message.delete();
			}	
		}.runTaskLaterAsynchronously(AlterEgoPlugin.INSTANCE, time / 50); //convert ms to ticks
	}

}
