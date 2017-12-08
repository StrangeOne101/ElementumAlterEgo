package com.strangeone101.elementumbot.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.common.io.Files;
import com.strangeone101.elementumbot.AlterEgoPlugin;
import com.strangeone101.elementumbot.util.ChatParser;

import net.md_5.bungee.api.chat.BaseComponent;

public class MatchesManager {
	
	public static File matchFile;
	public static final String splitter = ">>";
	
	public static Map<String, String> matches = new HashMap<String, String>();
	
	public MatchesManager() {
		matchFile = new File(AlterEgoPlugin.INSTANCE.getDataFolder(), "matches.txt");
		
		if (!matchFile.exists()) {
			try {
				String line = "#Format: Regex >> Reply\r\n(where.+did.+(alter.+ego|(the|that).+bot).+go)|where.+can.+find.+(alter.+ego|(the|that)) " + splitter + " &cI'm right here :D";
				Files.write(line, matchFile, Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		reloadMatches();
	}
	
	/**
	 * Returns a valid base component for the given text, or {@code null} 
	 * if no match is found.
	 * @param line The line to check
	 * @return The base component, or {@code null}
	 */
	public static BaseComponent getMatch(String line) {
		for (String match : matches.keySet()) {
			if (line.toLowerCase().matches(".*" + match + ".*")) {
				return ChatParser.parse(matches.get(match));
			}
		}
		return null;
	}

	public static void reloadMatches() {
		matches.clear();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(matchFile));
			
			String line = reader.readLine();
			while (line != null) {
				if (line.contains(splitter) && !line.startsWith("#")) {
					String key = line.split(splitter)[0].trim().toLowerCase();
					try {
						Pattern.compile(key);
					} catch (PatternSyntaxException e) {
						AlterEgoPlugin.INSTANCE.getLogger().severe("Pattern failed!");
						e.printStackTrace();
						line = reader.readLine();
						continue;
					}
					String value = line.split(splitter)[1].trim();
					matches.put(key, value);
				}
				line = reader.readLine();
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
