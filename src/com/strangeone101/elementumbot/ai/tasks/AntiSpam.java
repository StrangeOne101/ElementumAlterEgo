package com.strangeone101.elementumbot.ai.tasks;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.FakeCommandSender;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.LevenshteinDistance;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import sun.security.krb5.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

public class AntiSpam {

    public static int LOG_LENGTH = 5;

    private static Map<UUID, PlayerLog> playerLogs = new HashMap<UUID, PlayerLog>();

    public static List<String> commandsToListenFor = new ArrayList<>();

    static {
        commandsToListenFor.add("me");
        commandsToListenFor.add("eme");
        commandsToListenFor.add("essentials:me");
        commandsToListenFor.add("essentials:eme");
        commandsToListenFor.add("afk");
        commandsToListenFor.add("eafk");
        commandsToListenFor.add("essentials:afk");
        commandsToListenFor.add("essentials:eafk");
    }

    private static class PlayerLog {
        UUID uuid;
        int spamRating = 0;
        int lastLevel; //0 = none, 1 = silent but monitoring, 2 = verbal warning, 3 = real warning, 4 = small mute, 5 = medium mute (1h), 6 = large mute (24h),
        long lastRating;
        ArrayList<String> logs = new ArrayList<>();
        ArrayList<Long> logTimes = new ArrayList<>();

        public PlayerLog(Player player) {
            this.uuid = player.getUniqueId();
        }
    }


    public static void addLog(Player player, String string) {
        if (string.startsWith("/")) {
            if (!commandsToListenFor.contains(string.substring(1))) return;
        }

        if (!playerLogs.containsKey(player.getUniqueId())) {
            PlayerLog log = new PlayerLog(player);
            log.logs.add(string);
            log.logTimes.add(System.currentTimeMillis());
            playerLogs.put(player.getUniqueId(), log);
            return;
        }

        PlayerLog log = playerLogs.get(player.getUniqueId());
        int oldRating = log.spamRating;

        for (int i = 0; i < LOG_LENGTH && i < log.logs.size(); i++) {
            String compare = log.logs.get(i);
            int sizeDifference = Math.abs(compare.length() - string.length());
            int differenceBetween = LevenshteinDistance.distance(compare, string);

            double score = 0;
            long time = System.currentTimeMillis() - log.logTimes.get(i);

            if (differenceBetween == 0) {
                if (time < 30 * 1000) score += 1;
                if (time < 10 * 1000) score += 9;
            } else if ((differenceBetween <= 3 && string.length() > 10) || (differenceBetween < 2 && string.length() <= 10)) {
                if (time < 10 * 1000) score += 2;
            } else if ((differenceBetween <= 5 && string.length() > 10) || (differenceBetween < 4 && string.length() <= 10)) {
                if (time < 10 * 1000) score += 1;
            }

            if (time < 5 * 1000)  score *= 1.5;
            if (time < 3 * 1000)  score *= 1.5;
            if (time < 2 * 1000)  score *= 1.5;
            if (time < 1 * 1000)  score *= 1.5;
            if (time < 500)       score *= 2;
            if (time < 200)       score *= 5;

            int newScore = (int) Math.exp(Math.sqrt(log.spamRating) + Math.sqrt(score));
            log.spamRating = newScore;

            break;
        }

        if (log.spamRating - oldRating > 0) { //If they spam rating increased
            if (log.lastLevel == 0) {
                log.lastLevel++; //Increase but don't do anything yet
                log.lastRating = System.currentTimeMillis();
                return;
            } else if (log.lastLevel == 1) { //Verbal warn stage. Spammed at least 3 times.
                log.lastLevel++;

                sendMessageSync(player, ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfig().getString("AntiSpam.VerbalWarning", "&cPlease stop spamming so much. Continuing to do so will result in warns and possibly mutes.")));
            } else {
                if (log.lastLevel < 3 && log.spamRating <= 100) { //Real warn stage. For minor things but spammed at least 4 times
                    String warn = ConfigManager.getConfig().getString("AntiSpam.Warning", "/warn %player% Spamming").replaceAll("%player%", player.getName());
                    Bukkit.dispatchCommand(FakeCommandSender.self(), warn.startsWith("/") ? warn.substring(1) : warn);
                    log.lastLevel++;
                    log.lastRating = System.currentTimeMillis();
                } else if (log.lastLevel >= 3 && log.spamRating <= 200) { //Minor mute
                    String minor = ConfigManager.getConfig().getString("AntiSpam.Minor", "/tempmute %player% 10m Spamming").replaceAll("%player%", player.getName());
                    Bukkit.dispatchCommand(FakeCommandSender.self(), minor.startsWith("/") ? minor.substring(1) : minor);
                    log.lastLevel++;
                    log.lastRating = System.currentTimeMillis();
                } else if (log.lastLevel >= 4 && log.spamRating <= 400) { //Minor mute
                    String medium = ConfigManager.getConfig().getString("AntiSpam.Medium", "/tempmute %player% 1h Spamming").replaceAll("%player%", player.getName());
                    Bukkit.dispatchCommand(FakeCommandSender.self(), medium.startsWith("/") ? medium.substring(1) : medium);
                    log.lastLevel++;
                    log.lastRating = System.currentTimeMillis();
                } else if (log.lastLevel >= 5 && log.spamRating <= 800) { //Minor mute
                    String major = ConfigManager.getConfig().getString("AntiSpam.Major", "/tempmute %player% 24h Spamming").replaceAll("%player%", player.getName());
                    Bukkit.dispatchCommand(FakeCommandSender.self(), major.startsWith("/") ? major.substring(1) : major);
                    log.lastLevel++;
                    log.lastRating = System.currentTimeMillis();
                } else if (log.lastLevel < 5 && log.spamRating > 1000) { //If they are a freaking bot
                    String extreme = ConfigManager.getConfig().getString("AntiSpam.Extreme", "/ban %player% Excessive bot spamming").replaceAll("%player%", player.getName());
                    Bukkit.dispatchCommand(FakeCommandSender.self(), extreme.startsWith("/") ? extreme.substring(1) : extreme);
                    log.lastLevel++;
                    log.lastRating = System.currentTimeMillis();
                }
            }

        }

        log.logs.add(0, string);
        log.logTimes.add(0, System.currentTimeMillis());

        if (log.logs.size() > LOG_LENGTH) {
            log.logs.remove(LOG_LENGTH - 1);
            log.logTimes.remove(LOG_LENGTH - 1);
        }

    }

    public static boolean isEnabled() {
        return ConfigManager.getConfig().getBoolean("AntiSpam.Enabled");
    }

    private static void sendMessageSync(Player player, String message) {
        Bukkit.getScheduler().callSyncMethod(AlterEgoPlugin.INSTANCE, () -> {
            player.sendMessage(message);
            return null;
        });
    }
}
