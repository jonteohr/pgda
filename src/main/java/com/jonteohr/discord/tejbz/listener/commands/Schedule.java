package com.jonteohr.discord.tejbz.listener.commands;

import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.PropertyHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Schedule extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "schedule"))
			return;
		
		PropertyHandler prop = new PropertyHandler();
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("PGDA", null, App.authorImage);
		msg.setColor(App.color);
		msg.setTitle("This weeks schedule");
		msg.setImage(prop.getPropertyValue("schedule_url"));
		
		e.getChannel().sendMessage(msg.build()).queue();
	}
}
