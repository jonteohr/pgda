package com.jonteohr.discord.tejbz.listener.guild;

import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.twitch.Twitch;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildReady extends ListenerAdapter {
	public void onGuildReady(GuildReadyEvent e) {
		Twitch.initTwitch();
		
//		setPresence(Twitch.getSubscribers("tejbz"));
		setPresence(0);
	}
	
	/**
	 * 
	 * @param members a {@link java.lang.Integer Integer} of total member count.
	 */
	public void setPresence(int subs) {
		App.jda.getPresence().setActivity(Activity.watching(subs + " subs | Twitch.tv/Tejbz"));
	}
}
