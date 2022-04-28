package com.github.jonteohr.tejbz.discord.listener;

import java.util.Timer;
import java.util.TimerTask;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.discord.listener.commands.Video;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VideoAnnouncer extends ListenerAdapter {
	private static int count = 0;
	private static final int fire = 30;
	
	public void onMessageReceived(MessageReceivedEvent e) {
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
