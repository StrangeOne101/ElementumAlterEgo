package com.strangeone101.elementumbot.util;

public class StringUtil {
	
	public enum Direction {LEFT, RIGHT};
	
	public enum WordType {
		
		WORD("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"),
		WORD_WITH_NUMERICS("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"),
		WORD_WITH_NUMERICS_CHARS("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!~@#$%^&*(),.?/|;:'\"\\<>-+_="),
		WORD_WITH_CHARS("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!~@#$%^&*(),.?/|;:'\"\\<>-+_="),
		NUMERICS("0123456789"),
		NUMERICS_WITH_MATHCHARS("0123456789+-/*()^%");
		
		WordType(String chars) {
			this.chars = chars;
		}
		
		private String chars;
		
		public String getChars() {
			return chars;
		}
	}
	
	/**
	 * Gets the boundary of the next found word in the string provided.
	 * @param string The string to search
	 * @param startIndex Where to start searching
	 * @param direction The direction to search ({@link Direction.LEFT} or {@link Direction.RIGHT})
	 * @param type The {@link WordType} that we are searching with
	 * @return The index that the boundary is at, or -1 if something went wrong.
	 */
	public static int getBoundary(String string, int startIndex, Direction direction, WordType type) {
		int index = startIndex;
		if (direction == Direction.LEFT) {
			index--;
			while (index > 0) {
				if (!type.getChars().contains(string.charAt(index) + "")) {
					return index - 1;
				}
				index--;
			}
			return index;
		} else if (direction == Direction.RIGHT) {
			index++;
			while (index < string.length()) {
				if (!type.getChars().contains(string.charAt(index) + "")) {
					return index - 1;
				}
				index++;
			} 
			return index - 1;
		}
		return -1;
	}
	
	/**
	 * Insert a string into an existing string between 2 points. The two points will also be replaced.
	 * @param string
	 * @param startIndex
	 * @param endIndex
	 * @param replaceString
	 * @return
	 */
	public static String replaceBetween(String string, int startIndex, int endIndex, String replaceString) {
		String first = string.substring(0, startIndex);
		String last = string.substring(endIndex + 1);
		return first + replaceString + last;
	}

}
