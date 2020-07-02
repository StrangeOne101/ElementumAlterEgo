package com.strangeone101.elementumbot.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.config.ConfigManager;
import com.strangeone101.elementumbot.elementum.RankSync;
import com.strangeone101.elementumbot.util.Reactions;

import de.btobastian.javacord.entities.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class LinkCommand extends CommandRunnable {
	
	public static Map<UUID, String> links = new HashMap<UUID, String>();
	public static Map<Integer, String> linksBeingPrepared = new HashMap<Integer, String>(); //ID, User ID

	public LinkCommand() {
		super("link");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void runCommand(Command command) {
		if (links.values().contains(command.getSender().getId())) {
			command.getOriginal().reply("You already have an account linked! Use `!unlink` to unlink your account!");
			return;
		}
		
		if (command.getArguments().length == 0) {
			command.getOriginal().reply("Usage is `!link <mcuser>`. You must be in game when you run this command!");
			return;
		}
		
		if (Bukkit.getOfflinePlayer(command.getArguments()[0]) != null) {
			OfflinePlayer ofplayer = Bukkit.getOfflinePlayer(command.getArguments()[0]);
			if (ofplayer.isOnline()) {
				prepare((Player) ofplayer, command.getSender());
				command.getOriginal().addUnicodeReaction(Reactions.GREEN_TICK + "");
			} else {
				command.getOriginal().reply("Player is not online! Please run the command when you are online!");
			}
		} else {
			command.getOriginal().reply("User not found! Be sure you spelt your name correctly!");
			return;
		}

	}
	
	private void prepare(Player player, User sender) {
		player.sendMessage(AlterEgoPlugin.PREFIX + ChatColor.GREEN + " Discord Linking:");
		
		BaseComponent confirm = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "CONFIRM");
		BaseComponent deny = new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "REPORT ");
		BaseComponent base = new TextComponent(ChatColor.YELLOW + "Your account has been requested to link to discord user " + ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + ". If this is you, please click to ");
		BaseComponent mid = new TextComponent(ChatColor.YELLOW + ". If " + ChatColor.YELLOW + "not, please ");
		BaseComponent end = new TextComponent(ChatColor.YELLOW + "it.");
		
		int id = (int) (Math.random() * 10000);
		while (linksBeingPrepared.containsKey(id)) {
			id = (int) (Math.random() * 10000);
		}
		
		final int fid = id;
		
		linksBeingPrepared.put(fid, sender.getId());
		
		new BukkitRunnable() {
			@Override
			public void run() {
				linksBeingPrepared.remove(fid);
			}	
		}.runTaskLater(AlterEgoPlugin.INSTANCE, 20 * 60);
		
		confirm.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/link #" + id));
		confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.YELLOW + "Click to " + ChatColor.GREEN + "confirm")}));
		
		deny.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/link !" + id));
		deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(ChatColor.YELLOW + "Click to " + ChatColor.RED + "report")}));
		
		base.addExtra(confirm);
		base.addExtra(mid);
		base.addExtra(deny);
		base.addExtra(end);
		
		player.spigot().sendMessage(base);
	}
	
	public static boolean isLinked(UUID uuid) {
		return links.containsKey(uuid) && !links.get(uuid).equals("0");
	}
	
	public static boolean isLinked(String id) {
		return links.containsValue(id);
	}
	
	public static UUID getUUIDFromID(String id) {
		for (UUID uuid : LinkCommand.links.keySet()) {
			if (LinkCommand.links.get(uuid).equals(id)) {
				return uuid;
			}
		}
		return null;
	}
	
	public static void finalizeLink(int id, Player player) {
		User user = AlterEgoPlugin.API.getCachedUserById(linksBeingPrepared.get(id));
		boolean reward = !links.containsKey(player.getUniqueId());
		links.put(player.getUniqueId(), linksBeingPrepared.get(id));
		linksBeingPrepared.remove(id);
		
		user.sendMessage("Your account has been successfully linked to MC user " + player.getName());
		AlterEgoPlugin.INSTANCE.getLogger().info("Discord user " + user.getName() + "(" + user.getMentionTag() + ") successfully linked with MC user " + player.getName());
		ConfigManager.save();
		
		if (reward) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate key " + player.getName() + " epic");
			player.sendMessage(ChatColor.YELLOW + "You are rewarded 1 Epic Key for linking your account!");
		}
		
		if (Bukkit.getPluginCommand("LuckPerms") != null) {
			RankSync.syncRank(user);
		}
		
	}
	
	public static void reportLink(int id, Player player) {
		User user = AlterEgoPlugin.API.getCachedUserById(linksBeingPrepared.get(id));
		
		if (ConfigManager.isValidReportChannel()) {
			AlterEgoPlugin.API.getChannelById(ConfigManager.getReportChannel()).sendMessage("[MCL] Discord user " + user.getMentionTag() + " tried to link with player " + player.getName());
		}
		AlterEgoPlugin.INSTANCE.getLogger().warning("Discord user " + user.getName() + "(" + user.getMentionTag() + ") tried to link with player " + player.getName());
		
		linksBeingPrepared.remove(id);
	}

}
