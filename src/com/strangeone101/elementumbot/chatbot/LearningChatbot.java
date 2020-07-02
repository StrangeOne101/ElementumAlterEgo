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
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import com.strangeone101.elementumbot.AlterEgoPlugin;

public class LearningChatbot {
	
	public static LearningChatbot INSTANCE;
	
	/**
	 * Static definition of final word in a statement. It never has 
	 * any descendents, and concludes all statements. This is the only
	 * "starting knowledge" granted the bot.
	 */
	public static final ChatWord ENDWORD = new ChatWord("\n");

	/**
	 * The Brain of this operation.
	 */
	private ChatbotBrain brain;
	
	/**
	 * A list of things to parse when the brain becomes fully loaded.
	 */
	private Map<String, CommandSender> backlog = new HashMap<String, CommandSender>();
	
	/**
	 * The file used to store the brain
	 */
	private File file;
	
	/**
	 * Starts LearningChatbot with a new brain
	 */
	public LearningChatbot() {
		brain = new ChatbotBrain();
		file = new File(AlterEgoPlugin.INSTANCE.getDataFolder(), "brain.dat");
		
		INSTANCE = this;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (brain != null) {
					
					BrainMuxer.saveBrain(brain, file);
				}
			}
		}.runTaskTimerAsynchronously(AlterEgoPlugin.INSTANCE, 30 * 20 * 60L,  30 * 20 * 60L);
	}

	/**
	 * Starts LearningChatbot with restored brain.
	 */
	public LearningChatbot(File file) {
		this.file = file;
		
		new BukkitRunnable() {

			@Override
			public void run() {
				brain = BrainMuxer.loadBrain(file);
				if (brain == null) {
					brain = new ChatbotBrain();
				}
				
				if (!backlog.isEmpty()) {
					for (String s : backlog.keySet()) {
						intake(s, backlog.get(s));
					}
					backlog.clear();
				}
			}
		}.runTask(AlterEgoPlugin.INSTANCE);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if (brain != null) {
					BrainMuxer.saveBrain(brain, file);
				}
			}
		}.runTaskTimerAsynchronously(AlterEgoPlugin.INSTANCE, 30 * 20 * 60L,  30 * 20 * 60L);
		
		INSTANCE = this;
	}

	
	/*@Deprecated
	public void beginConversation() {
		Scanner dialog = new Scanner(System.in);

		boolean more = true;

		while (more) {
			System.out.print("    You? ");
			String input = null;
			try {
				input = dialog.nextLine();
			} catch (NoSuchElementException nsee) {
				more = false;
				continue;
			}

			if (input.equals("++done")) {
				more = false;
				continue;
			} else if (input.equals("++save")) {
				System.out.println("Filename to save to? (\"cancel\" to abort save)");
				String filename = dialog.nextLine();
				if (!filename.equalsIgnoreCase("cancel")) {
					if (BrainMuxer.saveBrain(brain, filename)) {
						System.out.println("Brain saved successfully!");
					} else {
						System.out.println("There was a problem saving the brain.");
					}
				} else {
					System.out.println("Save cancelled.");
				}
				continue;
			} else if (input.equals("++help")) {
				getHelp();
				continue;
			}else {
				brain.decay();
				brain.digestSentence(input);
			}

			System.out.print("Chatbot? ");
			System.out.println(brain.buildSentence());
		}
	}*/
	
	/**
	 * Parses the sentence provided and talks back to the user if they directly talk
	 * to the bot. This allows the bot to collect intel of conversations without having to
	 * form a reply. That way it's less intensive on the server, and so the bot can
	 * gather intel over time and allow it to get a good grasp on language before 
	 * people starting talking to it.
	 * <br><br>
	 * This task should be run Async to the server. This can take up to 5s to reply, and
	 * the server cannot be halted every time someone talks to the bot. 
	 * 
	 * @param sentence The sentence
	 * @param sender The sender
	 */
	public void intake(String sentence, CommandSender sender) {
		if (getBrain() == null) { //If the brain isn't loaded yet, store all the sentences to parse when loaded
			backlog.put(sentence, sender);
			return;
		}
		
		sentence = ChatColor.stripColor(sentence);
		
		boolean return_ = Pattern.compile("(?i)alter ?ego+( |, |: |. |... |\\\\? |! )+.*").matcher(sentence).find();
		
		getBrain().decay();
		
		if (return_) {
			sentence = sentence.replaceFirst("(?i)alter ?ego+( |, |: |. |... |\\? |! )", "");
			getBrain().digestSentence(sentence);
			String s = getBrain().buildSentence();
			
			new BukkitRunnable() { //We have to broadcast in sync with the server.
				@Override
				public void run() {
					Bukkit.broadcastMessage(AlterEgoPlugin.PREFIX + " " + ChatColor.RED + s);
				}
			}.runTask(AlterEgoPlugin.INSTANCE);
			
			AlterEgoPlugin.relay(AlterEgoPlugin.PREFIX + " " + ChatColor.RED + s);
		} else {
			getBrain().digestSentence(sentence);
		}
	}

	public ChatbotBrain getBrain() {
		return brain;
	}
	
	public String getBrainFileSize() {
		return NumberFormat.getInstance().format(file.length() / 1024) + "kb";
	}
	

	/**
	 * Help display
	 */
	/*public static void getHelp() {
		System.out.println("At any time during the conversation, type");
		System.out.println("   ++done");
		System.out.println("to exit without saving.");
		System.out.println("Or type");
		System.out.println("   ++save");
		System.out.println("to exit and save the brain.");
		System.out.println();
	}*/

	/**
	 * Get things started.
	 */
	/*public static void main(String[] args) {
		System.out.println("Welcome to the Learning Chatbot");
		System.out.println();
		getHelp();

		LearningChatbot lc = null;
		if (args.length > 0) {
			System.out.printf("Using %s as brain file, if possible.\n\n", args[0]);
			lc = new LearningChatbot(args[0]);
		} else {
			lc = new LearningChatbot();
		}
		lc.beginConversation();
	}*/
}


