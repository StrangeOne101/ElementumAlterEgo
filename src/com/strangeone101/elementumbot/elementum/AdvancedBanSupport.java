package com.strangeone101.elementumbot.elementum;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.command.LinkCommand;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.manager.PunishmentManager;
import me.leoko.advancedban.shaded.org.hsqldb.persist.Logger;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.Color;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AdvancedBanSupport implements Listener {

    private static Set<Long> mutedUsers = new HashSet<Long>();

    public AdvancedBanSupport() {
        Bukkit.getPluginManager().registerEvents(this, AlterEgoPlugin.INSTANCE);

        for (UUID uuid : LinkCommand.links.keySet()) {
            if (PunishmentManager.get().isMuted(uuid.toString())) {
                mutedUsers.add(LinkCommand.links.get(uuid)); //add them to the mute list
                long ticks = (PunishmentManager.get().getMute(uuid.toString()).getEnd() - System.currentTimeMillis()) / 50;
                Bukkit.getScheduler().runTaskLaterAsynchronously(AlterEgoPlugin.INSTANCE, () -> {
                    mutedUsers.remove(LinkCommand.links.get(uuid));
                }, ticks);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
	public void onPunishment(PunishmentEvent event) {
    	Punishment punishment = event.getPunishment();
    	
    	//This can only be implemented when the UUID code is fixed
//    	if (punishment.getType() == PunishmentType.BAN || event.getPunishment().getType() == PunishmentType.IP_BAN) {
//    		UUID uuid = fixUUID(punishment.getUuid());
//    		 if (LinkCommand.isLinked(uuid)) {
//    			 CompletableFuture<User> discordUser = AlterEgoPlugin.API.getUserById(LinkCommand.links.get(uuid));
//               discordUser.thenAccept((user) -> {
//                   AlterEgoPlugin.report("**" + punishment.getName() + "** | " + user.getDiscriminatedName() + " was banned by " + "**" + punishment.getOperator() + "**" + " for the reason: " + "_" + punishment.getReason() + "_");
//               });
//    		 }
//    
//
//    	}
		String message = "**" + punishment.getName() + "**" + " received a " + "**" + punishment.getType() + "**" + (punishment.getType().isTemp() ? " for **" + punishment.getDuration(true) + "**" : "") + " from " + "**" + punishment.getOperator() + "**" + " for the reason: " + "_" + punishment.getReason() + "_";
		Color color = punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.IP_BAN ||
                punishment.getType() == PunishmentType.TEMP_BAN || punishment.getType() == PunishmentType.TEMP_IP_BAN ? new Color(240, 72, 72)
                : punishment.getType() == PunishmentType.MUTE || punishment.getType() == PunishmentType.TEMP_MUTE ? new Color(255, 128, 0)
                : punishment.getType() == PunishmentType.WARNING || punishment.getType() == PunishmentType.TEMP_WARNING ? new Color(255, 255, 51)
                : new Color(32, 32, 32);

		EmbedBuilder embed = new EmbedBuilder()
                .setColor(color)
                .setTitle(punishment.getName() + " received a punishment.")
                .addField("Name", punishment.getName(), true)
                .addField("Type", punishment.getType().toString(), true)
                .addField("Operator", punishment.getOperator())
                .addField("Reason", punishment.getReason());

		if(punishment.getType().isTemp()) {
		    embed.addField("Duration", "For **" + punishment.getDuration(true) + "**");
        }

		AlterEgoPlugin.report(embed, message);
    	
		
	}
    
//    @EventHandler
//    public void onIveBeenNaughty(PunishmentEvent event) {
//        if (event.getPunishment().getType() == PunishmentType.BAN || event.getPunishment().getType() == PunishmentType.IP_BAN) {
//        	Punishment punishment = event.getPunishment();
//            UUID uuid = fixUUID(punishment.getUuid());
//            if (LinkCommand.isLinked(uuid)) {
//                CompletableFuture<User> discordUser = AlterEgoPlugin.API.getUserById(LinkCommand.links.get(uuid));
//                discordUser.thenAccept((user) -> {
//                    AlterEgoPlugin.report(event.getPunishment().getName() + " has been banned and is user " + user.getDiscriminatedName());
//                });
//
//            } }
//         if (event.getPunishment().getType() == PunishmentType.MUTE || event.getPunishment().getType() == PunishmentType.TEMP_MUTE) {
//        	 Punishment punishment = event.getPunishment();
//        	 UUID uuid = fixUUID(punishment.getUuid());
//            if (LinkCommand.isLinked(uuid)) {
//                mutedUsers.add(LinkCommand.links.get(uuid));
//
//                long ticks = (event.getPunishment().getEnd() - System.currentTimeMillis()) / 50;
//                Bukkit.getScheduler().runTaskLaterAsynchronously(AlterEgoPlugin.INSTANCE, () -> {
//                    mutedUsers.remove(LinkCommand.links.get(uuid));
//                }, ticks);
//            }
//        }
//    }
//
//    @EventHandler
//    public void revokePunishment(RevokePunishmentEvent event) {
//        if (event.getPunishment().getType() == PunishmentType.MUTE || event.getPunishment().getType() == PunishmentType.TEMP_MUTE) {
//       	 Punishment punishment = event.getPunishment();
//       	 UUID uuid = fixUUID(punishment.getUuid());
//            if (LinkCommand.isLinked(uuid) && mutedUsers.contains(uuid)) {
//                mutedUsers.remove(uuid);
//            }
//        }
//    }
    
    public UUID fixUUID(String s) {
    	System.out.println(s.length());
    return UUID.fromString(s.substring(8) + "-" + s.substring(8,12) + "-" + s.substring(12,16) + "-" + s.substring(16,20) + "-" + s.substring(20,32));
    }

    public static boolean isMutedInGame(long id) {
        return mutedUsers.contains(id);
    }
}
