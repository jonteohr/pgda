package com.github.condolent.tejbz.discord.listener.commands;

import com.github.condolent.tejbz.App;
import com.github.condolent.tejbz.twitch.Twitch;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Follow extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "follow"))
			return;
		
		if(args.length != 2) {
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " No channel specified.\n"
					+ "Correct usage: `!follow <yourchannel>`").queue();
			return;
		}
		
		if(Twitch.isFollowing(args[1])) {
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " You're following tejbz!").queue();
			return;
		}
		
		e.getChannel().sendMessage(e.getAuthor().getAsMention() + " You're not following tejbz...").queue();
	}
}
