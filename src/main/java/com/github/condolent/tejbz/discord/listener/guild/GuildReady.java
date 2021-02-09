package com.github.condolent.tejbz.discord.listener.guild;

import java.util.Timer;
import java.util.TimerTask;

import com.github.condolent.tejbz.App;
import com.github.condolent.tejbz.discord.listener.commands.Join;
import com.github.condolent.tejbz.discord.listener.roles.DefaultRoles;
import com.github.condolent.tejbz.twitch.Twitch;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildReady extends ListenerAdapter {

	public void onGuildReady(GuildReadyEvent e) {
		Twitch.initTwitch();

		App.guild = e.getGuild();
		App.general = e.getGuild().getTextChannelById("124204242683559938");
		App.twitchLog = e.getGuild().getTextChannelById("735122823017791578");
		Join.lobby = App.guild.getVoiceChannelById("124204246815080449");
		Join.queue = App.guild.getVoiceChannelById("732569438326489139");
		Join.live = App.guild.getVoiceChannelById("280730003484835860");

		setPresence(Twitch.getSubscribers("tejbz"));

		presenceTimer();

		// Set the role variables
		DefaultRoles.setRoles(e.getGuild());

		DefaultRoles.rewardCurrentMembers(e.getGuild());
	}

	/**
	 *
	 * @param subs a {@link java.lang.Integer Integer} of total member count.
	 */
	public void setPresence(int subs) {
		App.jda.getPresence().setActivity(Activity.watching(subs + " subs | Twitch.tv/Tejbz"));
	}

	public void presenceTimer() {
		Timer timer = new Timer();



		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				setPresence(Twitch.getSubscribers("tejbz"));
			}
		}, 10*60*1000, 10*60*1000);
	}
}
