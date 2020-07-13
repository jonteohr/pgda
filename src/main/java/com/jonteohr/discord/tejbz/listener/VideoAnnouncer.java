package com.jonteohr.discord.tejbz.listener;

import java.util.Timer;
import java.util.TimerTask;

import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.listener.commands.Video;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VideoAnnouncer extends ListenerAdapter {
	private static int count = 0;
	private static int fire = 35;
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		TextChannel channel = e.getGuild().getTextChannelById("699678530715254956");
		
		if(!e.getChannel().equals(channel))
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
				TextChannel channel = App.jda.getGuilds().get(0).getTextChannelById("699678530715254956");
				if(count >= fire) {
					Video.sendAd(channel);
					count = 0;
					return;
				}
			}
		}, 15*60*1000, 15*60*1000);
	}
}
