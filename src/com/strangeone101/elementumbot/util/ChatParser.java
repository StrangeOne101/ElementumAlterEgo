package com.strangeone101.elementumbot.util;

import org.bukkit.ChatColor;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatParser {
	
	/***
	 * Parses a line and converts it to a {@link ChatComponent}. This enables us
	 * to have hover events and click events as well.
	 * @param line The line to parse
	 * @return The final component to send to players
	 */
	public static BaseComponent parse(String line) {
		TextComponent base = new TextComponent("");
		
		String enclosedLine = "";
		String enclosedCommand = "";
		String enclosedHover = "";
		ChatParseState state = ChatParseState.NONE;
		
		line = ChatColor.translateAlternateColorCodes('&', line);
		
		if (!(line.contains("(") && line.contains(")") && line.contains("[") && line.contains("]"))) {
			BaseComponent[] componentLine = TextComponent.fromLegacyText(line); //Fixes the overflow of the color not working
			for (BaseComponent component : componentLine) {
				base.addExtra(component);
			}
			return base;
		}
		
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			
			if (c == '\\' && "()[]|".contains(String.valueOf(line.charAt(i + 1)))) continue; //If it's a backslash followed by a parse char, ignore it
			
			if (c == '(' && (i == 0 || line.charAt(i - 1) != '\\') && state == ChatParseState.NONE) { 
				if (!enclosedLine.equals("")) {
					BaseComponent[] componentLine = TextComponent.fromLegacyText(enclosedLine); //Fixes the overflow of the color not working
					for (BaseComponent component : componentLine) {
						base.addExtra(component);
					}
					enclosedLine = "";
				}
				state = ChatParseState.CHATLINE_READING;
				continue;
			} else if (c == ')' && line.charAt(i - 1) != '\\' && state == ChatParseState.CHATLINE_READING) {
				state = ChatParseState.NONE;
				continue;
			} else if (c == '[' && line.charAt(i - 1) != '\\' && state == ChatParseState.NONE) {
				state = ChatParseState.HOVER_READING;
				continue;
			} else if (c == ']' && line.charAt(i - 1) != '\\' && (state == ChatParseState.HOVER_READING || state == ChatParseState.COMMAND_READING)) {
				state = ChatParseState.NONE;
				
				BaseComponent[] componentLine = TextComponent.fromLegacyText(enclosedLine); //Fixes the overflow of the color not working
				for (BaseComponent component : componentLine) {
					if (!enclosedHover.equals("")) {
						component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(StringUtil.lengthSplit(enclosedHover, 60))}));
					} 
					
					if (!enclosedCommand.equals("")) {
						if (enclosedCommand.startsWith("http://") || enclosedCommand.startsWith("https://")) {
							component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, enclosedCommand));
						} else {
							component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, enclosedCommand));
						}
					}
					
					base.addExtra(component);
				}
				
				enclosedCommand = "";
				enclosedLine = "";
				enclosedHover = "";
				continue;
			} else if (c == '|' && line.charAt(i - 1) != '\\' && state == ChatParseState.HOVER_READING) {
				state = ChatParseState.COMMAND_READING;
				continue;
			}
			
			if (state == ChatParseState.COMMAND_READING) {
				enclosedCommand = enclosedCommand + c;
			} else if (state == ChatParseState.HOVER_READING) {
				enclosedHover = enclosedHover + c;
			} else {
				enclosedLine = enclosedLine + c;
			}
		}
		
		if (!enclosedLine.equals("")) {
			base.addExtra(new TextComponent(enclosedLine));
			enclosedLine = "";
		}
		
		return base;
	}
	
	private enum ChatParseState {
		NONE, CHATLINE_READING, HOVER_READING, COMMAND_READING;
	}

}