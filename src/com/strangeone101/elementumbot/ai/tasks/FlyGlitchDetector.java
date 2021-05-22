package com.strangeone101.elementumbot.ai.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.projectkorra.projectkorra.ability.FlightAbility;
import com.projectkorra.projectkorra.airbending.AirScooter;
import com.projectkorra.projectkorra.airbending.flight.FlightMultiAbility;
import com.projectkorra.projectkorra.util.FlightHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.ability.CoreAbility;
import com.projectkorra.projectkorra.airbending.AirSpout;
import com.projectkorra.projectkorra.airbending.Tornado;
import com.projectkorra.projectkorra.firebending.FireJet;
import com.projectkorra.projectkorra.waterbending.WaterSpout;
import com.strangeone101.elementumbot.AlterEgoPlugin;

import net.md_5.bungee.api.ChatColor;

public class FlyGlitchDetector implements Runnable {
	
	public List<UUID> warns = new ArrayList<UUID>();
	private static final String[] flyPermissions = new String[] {"essentials.fly"};
	public List<String> warnMessages = new ArrayList<String>();
	public List<String> annoyedMessages = new ArrayList<String>();
	private Random random;
	public List<Class<? extends CoreAbility>> flightAbilities = new ArrayList<Class<? extends CoreAbility>>();
	
	public FlyGlitchDetector() {
		warnMessages.add("Hey there! I just happened to notice that you have the fly glitch! Please run /flyglitch to turn it off");
		warnMessages.add("Nice flyglitch, dude! Do you mind running /flyglitch for me? Thanks!");
		warnMessages.add("Those are some cool moves! But, you seem to have flyglitch. Can you run /flyglitch please? Cheers");
		warnMessages.add("I'm a bot, bleep bloop! But as a bot, I noticed you have the flyglitch. Mind running /flyglitch to fix it?");
		warnMessages.add("Heyo! I noticed you got the flyglitch! Mind turning it off? /flyglitch will fix it!");
	
		annoyedMessages.add("Hey bub, I did ask you to turn off the flyglitch. But since you didn't listen to me, I'm gonna have to report you.");
		annoyedMessages.add("Dude, I didn't wanna have to do this, but I gotta report you for not fixing your flyglitch. Please do it when asked next time!");
		annoyedMessages.add("Dear oh dear... what do we have here? Since you didn't turn off the flyglitch, I had to report you. No hard feelings, alright?");
		annoyedMessages.add("Dang dude, why didn't you listen? All you had to do was run /flyglitch. It's been logged, so please do as I say next time.");

		flightAbilities.add(AirSpout.class);
		flightAbilities.add(WaterSpout.class);
		flightAbilities.add(FlightMultiAbility.class);
		flightAbilities.add(FireJet.class);
		flightAbilities.add(Tornado.class);
		flightAbilities.add(AirScooter.class);
		flightAbilities.add(FlightAbility.class);

		if (CoreAbility.getAbility("EarthSurf") != null) {
			flightAbilities.add(CoreAbility.getAbility("EarthSurf").getClass());
		}
		if (CoreAbility.getAbility("WaterFlow") != null) {
			flightAbilities.add(CoreAbility.getAbility("WaterFlow").getClass());
		}
		if (CoreAbility.getAbility("FireSki") != null) {
			flightAbilities.add(CoreAbility.getAbility("FireSki").getClass());
		}
		
		if (CoreAbility.getAbility("PlantWalk") != null) {
			flightAbilities.add(CoreAbility.getAbility("PlantWalk").getClass());
		}

		if (CoreAbility.getAbility("MetalHook") != null) {
			flightAbilities.add(CoreAbility.getAbility("MetalHook").getClass());
		}
		random = new Random();
		Bukkit.getScheduler().runTaskTimer(AlterEgoPlugin.INSTANCE, this, 20 * 30, 20 * 30);
	}

	@Override
	public void run() {
		playerloop:
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.isFlying() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {//Flight.hasFlight(player)) { //If they aren't flying or have temp flight
				warns.remove(player.getUniqueId());
				continue;
			}
	
			for (String perm : flyPermissions) {
				if (player.hasPermission(perm)) {
					warns.remove(player.getUniqueId());
					continue playerloop;
				}
			}
			
			if (Bukkit.getPluginManager().isPluginEnabled("Duels")) {
				if (AlterEgoPlugin.duels.getSpectateManager().isSpectating(player)) {
					continue playerloop;
				}
			}
			
			for (Class<? extends CoreAbility> abil: flightAbilities) { //If they are flying with an ability, that's okay!
				if (CoreAbility.hasAbility(player, abil)) continue playerloop;
			}
			
			if (warns.contains(player.getUniqueId())) {
				AlterEgoPlugin.report("**" + player.getName() + "**" + " is abusing flyglitch");
				player.sendMessage(AlterEgoPlugin.PREFIX + " " + ChatColor.RED + annoyedMessages.get(random.nextInt(annoyedMessages.size())));
				warns.remove(player.getUniqueId());
				Bukkit.dispatchCommand(player, "flyglitch");
			} else {
				warns.add(player.getUniqueId());
				player.sendMessage(AlterEgoPlugin.PREFIX + " " + ChatColor.RED + warnMessages.get(random.nextInt(warnMessages.size())));
			}
		}

	}

}
