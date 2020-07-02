/* "Zero"-knowledge Learning ChatBot  Copyright (C) 2014-2016 Daniel Boston (ProgrammerDan)
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the 
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.strangeone101.elementumbot.chatbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.strangeone101.elementumbot.AlterEgoPlugin;

/**
 * Brain Muxer handle demuxing and remuxing the brain from a savefile.
 */
public class BrainMuxer {

	public static boolean saveBrain(ChatbotBrain brain, File file) {
		try {
			ObjectOutputStream saveOut = new ObjectOutputStream(new FileOutputStream(file));
			saveOut.writeObject(brain);
			saveOut.flush();
			saveOut.close();
		} catch (IOException iof) {
			AlterEgoPlugin.INSTANCE.getLogger().warning("Could not access file: " + file.getName());
			iof.printStackTrace();
			return false;
		}

		return true;
	}

	public static ChatbotBrain loadBrain(File file) {
		try {
			// Check if file exists.
			if (!file.exists()) {
				AlterEgoPlugin.INSTANCE.getLogger().warning("File does not exist: " + file.getName());
				return null;
			}

			// load
			ObjectInputStream loadIn = new ObjectInputStream(new FileInputStream(file));
			ChatbotBrain chatbrain = (ChatbotBrain) loadIn.readObject();
			loadIn.close();

			return chatbrain;
		} catch (IOException iof) {
			AlterEgoPlugin.INSTANCE.getLogger().warning("Could not access file: " + file.getName());
			iof.printStackTrace();
			return null;
		} catch (ClassNotFoundException cnfe) {
			AlterEgoPlugin.INSTANCE.getLogger().warning("That file does not contain a valid brain: " + file.getName());
			cnfe.printStackTrace();
			return null;
		}
	}

}

