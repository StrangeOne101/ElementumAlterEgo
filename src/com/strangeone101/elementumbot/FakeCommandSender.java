package com.strangeone101.elementumbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.strangeone101.elementumbot.command.LinkCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;

public class FakeCommandSender implements ConsoleCommandSender {
	
	private User user;
	private Message message;
	
	private BukkitTask messageSendFunction;
	private List<String> messages = new ArrayList<String>();
	
	public FakeCommandSender(User user, Message message) {
		this.user = user;
		this.message = message;
	}
	
	protected void sendDiscordMessage(String text) {
		messages.add("[MCC] " + MessageHandler.format(ChatColor.stripColor(text)));
		
		FakeCommandSender instance = this;
		if (messageSendFunction == null) {
			messageSendFunction = new BukkitRunnable() {

				@Override
				public void run() {
					message.getChannel().sendMessage(String.join("\n", instance.messages));
					instance.messages.clear();
					instance.messageSendFunction = null;
				}
				
			}.runTaskLater(AlterEgoPlugin.INSTANCE, 2L);
		}
	}

	@Override
	public String getName() {
		String userS = user.getName();
		if (LinkCommand.isLinked(user.getId())) userS = Bukkit.getOfflinePlayer(LinkCommand.getUUIDFromID(user.getId())).getName();

		if (userS.length() >= 16) userS = userS.substring(0, 16);
		else userS = "#" + userS;

		return userS;
	}

	@Override
	public Spigot spigot() {
		return null;
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public void sendMessage(String arg) {
		sendDiscordMessage(arg);
	}

	@Override
	public void sendMessage(String[] arg0) {
		for (String s : arg0) {
			sendDiscordMessage(s);
		}
		
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	@Override
	public boolean hasPermission(String arg0) {
		return true;
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return true;
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return true;
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return true;
	}

	@Override
	public void recalculatePermissions() {
		
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public void setOp(boolean arg0) {
		
	}

	@Override
	public void abandonConversation(Conversation arg0) {
		
		
	}

	@Override
	public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
		
	}

	@Override
	public void acceptConversationInput(String arg0) {
		
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		return false;
	}

	@Override
	public boolean isConversing() {
		return false;
	}

	@Override
	public void sendRawMessage(String arg0) {
		sendDiscordMessage(arg0);
	}

}
