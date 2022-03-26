package com.github.jonteohr.tejbz.discord.listener.commands;

import com.github.jonteohr.tejbz.App;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Social extends ListenerAdapter {
	public void onMessageReceived(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "social"))
			return;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("PGDA", null, App.authorImage);
		msg.setColor(App.color);
		msg.setTitle("Social Links");
		
		msg.addField("Twitter", "[@tejbz](https://twitter.com/tejbz)", true);
		msg.addField("Facebook", "[/tejbz](https://www.facebook.com/tejbz)", true);
		msg.addField("Instagram", "[@tejbz](https://www.instagram.com/tejbz/)", true);
		msg.addField("YouTube", "[/tejbztejbz](https://www.youtube.com/c/tejbztejbz)", true);
		
		e.getChannel().sendMessage(e.getAuthor().getAsMention()).queue();
		e.getChannel().sendMessageEmbeds(msg.build()).queue();
	}
}
