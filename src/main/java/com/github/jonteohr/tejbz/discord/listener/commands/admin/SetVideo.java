package com.github.jonteohr.tejbz.discord.listener.commands.admin;

import com.github.jonteohr.tejbz.PermissionHandler;
import com.github.jonteohr.tejbz.PropertyHandler;
import com.github.jonteohr.tejbz.web.WebLog;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SetVideo extends ListenerAdapter {
	public static void setVideo(SlashCommandInteractionEvent e, InteractionHook hook) {
		PropertyHandler prop = new PropertyHandler();
		PermissionHandler perm = new PermissionHandler();
		
		if(!perm.isMod(e.getMember()))
			return;
		
		if(e.getOptions().size() < 1) {
			hook.sendMessage("Invalid usage.\nCorrect usage: `!setvid <youtubeURL>`").queue();
			return;
		}
		
		String url = e.getOption("video_url").getAsString();
		
		if(prop.setProperty("recent_video", url)) {
			hook.sendMessage("Successfully saved!").queue();
			WebLog.addToWeblog("DISCORD", e.getUser().getAsTag(), "Updated the latest video: <a href='" + url + "'>New Video</a>");
		} else {
			hook.sendMessage("Failed to save...").queue();
		}
	}
}
