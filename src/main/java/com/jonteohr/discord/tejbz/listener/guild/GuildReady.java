package com.jonteohr.discord.tejbz.listener.guild;

import java.util.Timer;
import java.util.TimerTask;

import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.twitch.Twitch;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildReady extends ListenerAdapter {
	
	public void onGuildReady(GuildReadyEvent e) {
		Twitch.initTwitch();
		
		App.guild = e.getGuild();
		App.general = e.getGuild().getTextChannelById("124204242683559938");
		App.twitchLog = e.getGuild().getTextChannelById("735122823017791578");
//		App.general = e.getGuild().getTextChannelById("732241829877252096");
		
		setPresence(Twitch.getSubscribers("tejbz"));
		
		presenceTimer();
	}
	
	/**
	 * 
	 * @param members a {@link java.lang.Integer Integer} of total member count.
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
