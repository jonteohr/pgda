package com.jonteohr.discord.tejbz.listener.commands.admin;

import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.PermissionHandler;
import com.jonteohr.discord.tejbz.PropertyHandler;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SetVideo extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		PropertyHandler prop = new PropertyHandler();
		PermissionHandler perm = new PermissionHandler();
		
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "setvid"))
			return;
		
		if(!perm.isMod(e.getMember()))
			return;
		
		if(args.length < 2) {
			e.getAuthor().openPrivateChannel().complete().sendMessage("Invalid usage.\nCorrect usage: `!setvid <youtubeURL>`").queue();
			return;
		}
		
		String url = args[1];
		
		if(prop.setProperty("recent_video", url)) {
			e.getAuthor().openPrivateChannel().complete().sendMessage("Successfully saved!").queue();
		} else {
			e.getAuthor().openPrivateChannel().complete().sendMessage("Failed to save...").queue();
		}
	}
}
