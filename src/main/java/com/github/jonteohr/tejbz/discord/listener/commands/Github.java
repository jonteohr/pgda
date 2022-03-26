package com.github.jonteohr.tejbz.discord.listener.commands;

import com.github.jonteohr.tejbz.App;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Github extends ListenerAdapter {
	public void onMessageReceived(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "github"))
			return;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("PGDA", null, App.authorImage);
		msg.setTitle("PGDABot on Github", "https://github.com/condolent/pgda");
		msg.setColor(App.color);
		msg.setDescription("PGDABot is an open sourced Java development project. We're open to contributions from the community!");
		
		e.getChannel().sendMessageEmbeds(msg.build()).queue();
		return;
	}
}
