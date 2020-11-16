package com.github.condolent.tejbz.discord.listener;

import java.util.Timer;
import java.util.TimerTask;

import com.github.condolent.tejbz.App;
import com.github.condolent.tejbz.discord.listener.commands.Video;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VideoAnnouncer extends ListenerAdapter {
	private static int count = 0;
	private static final int fire = 30;
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if(!e.getGuild().equals(App.guild))
			return;
		
		if(!e.getChannel().equals(App.general))
			return;
		
		if(e.getAuthor().isBot())
			return;
		
		if(count < fire) {
			count++;
			return;
		}
	}
	
	public static void videoTimer() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(count >= fire) {
					Video.sendAd(App.general);
					count = 0;
					return;
				}
			}
		}, 7*60*1000, 7*60*1000);
	}
}
