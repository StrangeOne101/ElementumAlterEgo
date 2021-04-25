package com.strangeone101.elementumbot.commandmc;

import com.strangeone101.elementumbot.config.ConfigManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.strangeone101.elementumbot.AlterEgoPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.Color;
import java.util.Arrays;

public class DiscordReportMCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
		if (!(sender.hasPermission("alterego.command.discordreport"))) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to run this command!");
			return true;
		}

		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You cannot use this command through the console.");
			return true;
		}

		if (args.length < 1 || args[0].equalsIgnoreCase(sender.getName())) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.Usage")
					.replaceAll("\\n", "\n")));
			return true;
		}

		if (Bukkit.getPlayer(args[0]) == null) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.Offline")
					.replaceAll("%player%", args[0])
					.replaceAll("\\n", "\n")));
			return true;
		}

		Player player = (Player) sender;
		ConfigurationSection section = ConfigManager.defaultConfig.get().getConfigurationSection("ReportReasons");

		if(args.length < 2) {
			ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta) stack.getItemMeta();

			ComponentBuilder builder = new ComponentBuilder(ChatColor.RED + "" + ChatColor.BOLD + "Report " + args[0] + "\n");
			for (String key : section.getKeys(false)) {
				ConfigurationSection reason = section.getConfigurationSection(key);
				String reasonName = reason.getString("Name");
				builder.append(ChatColor.BLACK + "\n> ");
				builder.append(
						new ComponentBuilder(ChatColor.BLACK + reasonName)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + args[0] + " " + key))
						.create()
				);
			}
			meta.spigot().addPage(builder.create());

			meta.setTitle("Report");
			meta.setAuthor("Server");

			stack.setItemMeta(meta);
			player.openBook(stack);
			return true;
		}

		String reasonKey = args[1];
		if(!section.contains(reasonKey)) {
			sender.sendMessage(ChatColor.RED + "Invalid reason.");
			return true;
		}

		ConfigurationSection reason = section.getConfigurationSection(reasonKey);

		if(args.length < 3) {
			ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta) stack.getItemMeta();

			ComponentBuilder builder = new ComponentBuilder(ChatColor.RED + "" + ChatColor.BOLD + reason.getString("Question") + "\n");
			for (String choice : reason.getStringList("Choices")) {
				builder.append(ChatColor.BLACK + "\n> ");
				builder.append(
						new ComponentBuilder(ChatColor.BLACK + choice)
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report " + args[0] + " " + args[1] + " " + choice))
								.create()
				);
			}
			meta.spigot().addPage(builder.create());

			meta.setTitle("Report");
			meta.setAuthor("Server");

			stack.setItemMeta(meta);
			player.openBook(stack);
			return true;
		}

		String details = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
		if(!reason.getStringList("Choices").contains(details)) {
			sender.sendMessage(ChatColor.RED + "Invalid details.");
			return true;
		}

		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.Success")
				.replaceAll("%player%", args[0])
				.replaceAll("\\n", "\n")));

		String message = sender.getName() + " has reported user " + args[0] + " for the following reason: " + reason.getString("Name") + " " + details;

		String[] rgb = reason.getString("Color").split(",");
		Color color = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));

		AlterEgoPlugin.report(
				new EmbedBuilder()
						.setColor(color)
						.setTitle("Report by " + sender.getName())
						.setDescription("[Report] `" + sender.getName() + "` has reported user `" + args[0] + "`.")
						.addField("Reason", reason.getString("Name"))
						.addField(reason.getString("Question"), details),
				message
		);

		String messageToReported = ChatColor.translateAlternateColorCodes('&', ConfigManager.defaultConfig.get().getString("Report.IveBeenNaughty")
				.replaceAll("%player%", sender.getName())
				.replaceAll("%reason%", String.join(" ", Arrays.copyOfRange(args, 1, args.length)))
				.replaceAll("\\n", "\n"));
		Bukkit.getPlayer(args[0]).sendMessage(messageToReported);
		return true;
	}

}
