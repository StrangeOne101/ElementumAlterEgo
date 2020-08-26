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
        String s = counter % 2 == 0 ? "on Elementum | " + Bukkit.getOnlinePlayers().size() + " online" : (counter % 4 == 1 ? "on Elementum | Join @ elementum.me" : "on Elementum | Coming on?");
        AlterEgoPlugin.INSTANCE.API.updateActivity(ActivityType.PLAYING, s);
        counter++;
    }
} 
