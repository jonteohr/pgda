package com.github.jonteohr.tejbz.discord.listener.commands;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.PropertyHandler;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Video extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "video"))
			return;
		
		PropertyHandler prop = new PropertyHandler();
		
		String url = prop.getPropertyValue("recent_video");
		
		e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Check out the latest video on the YouTube channel!\n" + url).queue();
	}
	
	public static void sendAd(TextChannel channel) {
		PropertyHandler prop = new PropertyHandler();
		
		String url = prop.getPropertyValue("recent_video");
		
		channel.sendMessage("Check out the latest video on the YouTube channel!\n" + url).queue();
	}
}
