package com.strangeone101.elementumbot.ai.tasks;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.user.UserStatus;

public class PlayTitleUpdater extends BukkitRunnable {

    int counter;

    public PlayTitleUpdater() {
        this.runTaskTimerAsynchronously(AlterEgoPlugin.INSTANCE, 20L, 20 * 30);
    }
    
    @Override
    public void run() {
        String s = "on Elementum.me | " + Bukkit.getOnlinePlayers().size() + " online";
        AlterEgoPlugin.INSTANCE.API.updateActivity(ActivityType.PLAYING, s);
        counter++;
    }
} 
