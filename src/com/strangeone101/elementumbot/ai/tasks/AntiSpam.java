package com.strangeone101.elementumbot.ai.tasks;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.FakeCommandSender;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.util.LevenshteinDistance;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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
        commandsToListenFor.add("afk");
        commandsToListenFor.add("eafk");
        commandsToListenFor.add("suicide");
        commandsToListenFor.add("esuicide");
    }

    private static class PlayerLog {
        UUID uuid;
        int spamRating = 0;
        int lastLevel; //0 = none, 1 = silent but monitoring, 2 = verbal warning, 3 = real warning, 4 = small mute, 5 = medium mute (1h), 6 = large mute (24h),
        long lastRating;
        long lastMessage;
        ArrayList<String> logs = new ArrayList<>();
        ArrayList<Long> logTimes = new ArrayList<>();
        ArrayList<RatingLog> ratingLogs = new ArrayList<>();


        public PlayerLog(Player player) {
            this.uuid = player.getUniqueId();
        }
    }

    private static class RatingLog {
        int score;
        int difference;
        long timeDiff;
        int level;
        String oldMessage, newMessage;
    }


    public static void addLog(Player player, String string) {
        if (string.startsWith("/")) {
            if (!commandsToListenFor.contains(string.toLowerCase().substring(1).split(" ")[0].split(":")[0])) return;
        }

        if (!playerLogs.containsKey(player.getUniqueId())) {
            PlayerLog log = new PlayerLog(player);
            log.logs.add(string);
            log.logTimes.add(System.currentTimeMillis());
            log.lastMessage = System.currentTimeMillis();
            playerLogs.put(player.getUniqueId(), log);
            return;
        }

        PlayerLog log = playerLogs.get(player.getUniqueId());
        int oldRating = log.spamRating;

        if (log.spamRating > 0 && (log.lastRating == 0 || log.lastRating + 10_000 > System.currentTimeMillis())) { //Only decrease if they have a rating and it's been more than 10 seconds since they were marked last
            if (log.lastRating + 1000 * 60 * 2 > System.currentTimeMillis()) {
                log.lastLevel -= (int)((System.currentTimeMillis() - log.lastRating + 1000 * 60 * 60) / 1000 * 60 * 60);
                if (log.lastLevel < 1) log.lastLevel = 1;
            }
        }

        log.spamRating -= (int)((System.currentTimeMillis() - log.lastMessage) / 250); //Take away some spam rating for every 4th of a second that has passed
        if (log.spamRating < 0) log.spamRating = 0;

        int newScore = 0;
        RatingLog newLog = new RatingLog();
        for (int i = 0; i < LOG_LENGTH && i < log.logs.size(); i++) {
            String compare = log.logs.get(i);
            int size = compare.length() > string.length() ? compare.length() : string.length();
            int differenceBetween = LevenshteinDistance.distance(compare, string); //The lower, the more spammy

            double score = 0;
            long time = System.currentTimeMillis() - log.logTimes.get(i);

            if (differenceBetween == 0) { //Exactly the same as the message we are checking.
                if (time < 30 * 1000) score += 3;
                if (time < 10 * 1000) score += 12;
            } else if ((differenceBetween <= 2 && size <= 4) || (differenceBetween <= 3 && size <= 5)) {
                if (time < 10 * 1000) score += differenceBetween / (size + (size == 1 ? 1 : 0)) * 6; //small patch to people that use 1 letter.
            } else if ((differenceBetween <= 3 && size > 10) || (differenceBetween < 2 && size <= 10)) {
                if (time < 10 * 1000) score += 4;
            } else if ((differenceBetween <= 5 && size > 10) || (differenceBetween < 4 && size <= 10)) {
                if (time < 10 * 1000) score += 2;
            } else {
                if (time < 1000) score += 1;
            }

            score /= (i + 1); //The further away the test, we will reduce the score
            if (string.toUpperCase().equals(string) && string.matches(".*[A-z].*")) score *= 2; //double score for full caps

            if (time < 200)       score *= 20;
            else if (time < 500)       score *= 7;
            else if (time < 1 * 1000)  score *= 3;
            else if (time < 2 * 1000)  score *= 1.5;
            //else if (time < 3 * 1000)  score *= 2;

            if (score > newScore) {
                newScore = (int) score;
                newLog.score = newScore;
                newLog.level = log.lastLevel + 1;
                newLog.difference = differenceBetween;
                newLog.timeDiff = time;
                newLog.newMessage = string;
                newLog.oldMessage = compare;
            }
        }

        log.spamRating = log.spamRating + newScore;

        //AlterEgoPlugin.INSTANCE.getLogger().info("Score: " + newScore + " | NewRating: " + log.spamRating + " | Level: " + (log.lastLevel));
        //AlterEgoPlugin.INSTANCE.getLogger().info(string);

        if (log.spamRating - oldRating > 0 && newScore >= 15) { //If they spam rating increased
            log.ratingLogs.add(newLog);
            if (log.lastLevel == 0) {
                log.lastLevel++; //Increase but don't do anything yet
                log.lastRating = System.currentTimeMillis();
            } else if (log.lastLevel == 1) { //Verbal warn stage. Spammed at least 3 times.
                log.lastLevel++;
                log.lastRating = System.currentTimeMillis();
                sendMessageSync(player, ChatColor.translateAlternateColorCodes('&', ConfigManager.getConfig().getString("AntiSpam.VerbalWarning", "&cPlease stop spamming so much. Continuing to do so will result in warns and possibly mutes.")));
            } else if (log.lastLevel == 2) {
                log.lastLevel++;
                log.lastRating = System.currentTimeMillis();

                if (log.spamRating > 1000) { //If they are a bot
                    String extreme = ConfigManager.getConfig().getString("AntiSpam.Extreme", "/ban %player% Excessive bot spamming").replaceAll("%player%", player.getName());
                    sendCommandSync(extreme);
                    AlterEgoPlugin.report(player.getName() + " was banned for excessive spamming");
                } else {
                    String warn = ConfigManager.getConfig().getString("AntiSpam.Warning", "/warn %player% Spamming").replaceAll("%player%", player.getName());
                    sendCommandSync(warn);
                    AlterEgoPlugin.report(player.getName() + " received a warning for spamming");
                }
            } else {
                if (log.lastLevel >= 3 && log.spamRating <= 200) { //Minor mute
                    String minor = ConfigManager.getConfig().getString("AntiSpam.Mute.Minor", "/tempmute %player% 10m Spamming").replaceAll("%player%", player.getName());
                    sendCommandSync(minor);
                    AlterEgoPlugin.report(player.getName() + " received a minor mute for spamming (10m)");
                } else if ((log.lastLevel >= 4 && log.spamRating <= 400) || (log.lastLevel == 3 && log.spamRating > 200)) { //Minor mute
                    String medium = ConfigManager.getConfig().getString("AntiSpam.Mute.Medium", "/tempmute %player% 1h Spamming").replaceAll("%player%", player.getName());
                    sendCommandSync(medium);
                    AlterEgoPlugin.report(player.getName() + " received a medium mute for spamming (1h)");
                } else if (log.lastLevel >= 5 && log.spamRating <= 800 || (log.lastLevel == 3 && log.spamRating > 500)) { //Minor mute
                    String major = ConfigManager.getConfig().getString("AntiSpam.Mute.Major", "/tempmute %player% 24h Spamming").replaceAll("%player%", player.getName());
                    sendCommandSync(major);
                    AlterEgoPlugin.report(player.getName() + " received a major mute for spamming (24h)");
                } else if (log.lastLevel < 5 && log.spamRating > 1000) { //If they are a freaking bot
                    String extreme = ConfigManager.getConfig().getString("AntiSpam.Mute.Extreme", "/ban %player% Excessive bot spamming").replaceAll("%player%", player.getName());
                    sendCommandSync(extreme);
                    AlterEgoPlugin.report(player.getName() + " was banned for excessive spamming");
                }
                log.lastLevel++;
                log.lastRating = System.currentTimeMillis();
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

    private static void sendCommandSync(String command) {
        Bukkit.getScheduler().callSyncMethod(AlterEgoPlugin.INSTANCE, () -> {
            final String commandLine = command.startsWith("/") ? command.substring(1) : command;
            System.out.println("[AlterEgo] issued server command /" + (commandLine));
            Bukkit.dispatchCommand(FakeCommandSender.self(), commandLine);
            return null;
        });
    }

    public static String getStats(OfflinePlayer player) {
        String string = "";
        PlayerLog log = playerLogs.get(player.getUniqueId());
        if (log == null) return ChatColor.RED + "No log found for player " + player.getName();
        string += ChatColor.YELLOW + "---- AntiSpam stats for " + player.getName() + " ----\n";
        string += ChatColor.YELLOW + "Spam rating: " + log.spamRating + "\n";
        string += ChatColor.YELLOW + "Spam level: " + log.lastLevel + '\n';
        if (log.logs.size() != 0) {
            string += ChatColor.YELLOW + "Last logged messages: \n";
            for (String s : log.logs) {
                string += ChatColor.YELLOW + "- " + s + "\n";
            }

            if (log.ratingLogs.size() != 0) {
                string += ChatColor.YELLOW + "Last spam rating recordings: \n";

                for (int i = 0; i < log.ratingLogs.size(); i++) {
                    RatingLog l = log.ratingLogs.get(log.ratingLogs.size() - i - 1); //Get in reverse order
                    String time = ((double)l.timeDiff / 1000D) + "s";
                    string += ChatColor.YELLOW + "- " + ChatColor.RED + "+" + l.score + ChatColor.YELLOW
                            + " (" + ChatColor.RED + l.difference +ChatColor.YELLOW + " difference) @ "
                            + l.level + ChatColor.YELLOW + "L " + ChatColor.RED + time + ChatColor.YELLOW + " in between\n"
                            + ChatColor.RED + l.oldMessage + "\n" + ChatColor.GREEN + l.newMessage + "\n";
                }
            }
        }

        return string;
    }

    public static boolean reset(UUID uuid) {
        if (playerLogs.containsKey(uuid)) {
            playerLogs.remove(uuid);
            return true;
        }
        return false;
    }
}
