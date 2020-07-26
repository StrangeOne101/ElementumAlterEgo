package com.strangeone101.elementumbot.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

import com.google.common.collect.Lists;
import com.strangeone101.elementumbot.AlterEgoPlugin;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;

public class DiscordUtil {
	
	/**
	 * Gets the top (most important) role of a discord user
	 * @param user The user
	 * @return The top {@link Role}, or <code>null</code> if they have no roles
	 */
	public static Role getTopRole(User user) {
		if (user.getRoles(AlterEgoPlugin.SERVER).isEmpty()) {
			return null;
		}
		
		Comparator<Role> comparator = new Comparator<Role>() {

			@Override
			public int compare(Role r1, Role r2) {
				return r2.getPosition() - r1.getPosition();
			}
		};
		
		List<Role> roles = (List<Role>) Lists.newArrayList(user.getRoles(AlterEgoPlugin.SERVER)); //Get all roles of user

		Collections.sort(roles, comparator); //Sort the roles by their position
		
		return roles.get(0); //Get first element, which is the top role
	}
	
	/**
	 * This generates a {@link ChatColor} from a provided discord {@link Role}. 
	 * It compares the RGB values to find the closest color code that matches it.
	 * @param role The role
	 * @return The closest working {@link ChatColor}
	 */
	public static ChatColor getColorOfRole(Role role) {
		if (!role.getColor().isPresent()) return ChatColor.DARK_GRAY;

		Map<String, ChatColor> mcColorCodeMap = new HashMap<String, ChatColor>();
		mcColorCodeMap.put("000000", ChatColor.BLACK);
		mcColorCodeMap.put("000AAA", ChatColor.DARK_BLUE);
		mcColorCodeMap.put("00AA00", ChatColor.DARK_GREEN);
		mcColorCodeMap.put("00AAAA", ChatColor.DARK_AQUA);
		mcColorCodeMap.put("AA0000", ChatColor.DARK_RED);
		mcColorCodeMap.put("AA00AA", ChatColor.DARK_PURPLE);
		mcColorCodeMap.put("FFAA00", ChatColor.GOLD);
		mcColorCodeMap.put("AAAAAA", ChatColor.GRAY);
		mcColorCodeMap.put("555555", ChatColor.DARK_GRAY);
		mcColorCodeMap.put("5555FF", ChatColor.BLUE);
		mcColorCodeMap.put("55FF55", ChatColor.GREEN);
		mcColorCodeMap.put("55FFFF", ChatColor.AQUA);
		mcColorCodeMap.put("FF5555", ChatColor.RED);
		mcColorCodeMap.put("FF55FF", ChatColor.LIGHT_PURPLE);
		mcColorCodeMap.put("FFFF55", ChatColor.YELLOW);
		mcColorCodeMap.put("FFFFFF", ChatColor.WHITE);

		int red = role.getColor().get().getRed();
		int green = role.getColor().get().getGreen();
		int blue = role.getColor().get().getBlue();
		
		String closetColor = "";
		int closestDiff = Integer.MAX_VALUE;
		
		//This goes through each color and works out which color is closest based on the numerical RGB values
		//Once the loop ends, we now have the closet color stored in `closestColor` (the key for the chatcolor map)
		for (String colorString : mcColorCodeMap.keySet()) {
			java.awt.Color color = new java.awt.Color (
		            Integer.valueOf(colorString.substring( 0, 2 ), 16),
		            Integer.valueOf(colorString.substring( 2, 4 ), 16),
		            Integer.valueOf(colorString.substring( 4, 6 ), 16));
			
			int redDiff = red - color.getRed();
			int greenDiff = green - color.getGreen();
			int blueDiff = blue - color.getBlue();
			int totalDiff = Math.abs(redDiff) + Math.abs(greenDiff) + Math.abs(blueDiff);
			
			if (totalDiff < closestDiff) { //If the difference of the last color checked is less than the last one
				closestDiff = totalDiff;
				closetColor = colorString; //Update the new closet color
			}
		}
	
		return mcColorCodeMap.get(closetColor);
	}

}
