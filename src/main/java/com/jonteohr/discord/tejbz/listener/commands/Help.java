package com.jonteohr.discord.tejbz.listener.commands;

import java.util.Random;

import com.jonteohr.discord.tejbz.App;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Help extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "help"))
			return;
		
		String[] motd = {
				"That's Pretty God Damn Awesome!",
				"To Tha' Face, To Tha' Face"
		};
		
		Random rand = new Random();
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("PGDA", null, App.authorImage);
		msg.setColor(App.color);
		msg.setTitle("Available commands");
		
		msg.addField("Command", ""
				+ "`!social`\n"
				+ "`!stream`\n"
				+ "`!video`\n"
				+ "`!schedule`\n"
				+ "`!join`", true);
		msg.addField("Description", ""
				+ "Links to Tejbz social accounts.\n"
				+ "Current information about the stream.\n"
				+ "Link to the most recent video.\n"
				+ "This weeks' streaming schedule.\n"
				+ "Join the queue for gaming with Tejbz.", true);
		msg.setFooter("" + motd[rand.nextInt(motd.length)]);
		
		e.getChannel().sendMessage(msg.build()).queue();
	}
}
