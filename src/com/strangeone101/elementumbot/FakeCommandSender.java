package com.strangeone101.elementumbot;

import java.util.Set;

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

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;

public class FakeCommandSender implements ConsoleCommandSender {
	
	private User user;
	private Message message;
	
	public FakeCommandSender(User user, Message message) {
		this.user = user;
		this.message = message;
	}

	@Override
	public String getName() {
		return "Alter Ego (" + user.getName() + ")";
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public void sendMessage(String arg) {
		message.reply("[MCC] " + MessageHandler.format(ChatColor.stripColor(arg)));
	}

	@Override
	public void sendMessage(String[] arg0) {
		for (String s : arg0) {
			message.reply("[MCC] " + MessageHandler.format(ChatColor.stripColor(s)));
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
		message.reply("[MCC] " + MessageHandler.format(ChatColor.stripColor(arg0)));
	}

}
