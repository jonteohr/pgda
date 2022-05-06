package com.github.jonteohr.tejbz.discord.listener.commands.admin;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.jonteohr.tejbz.PermissionHandler;
import com.github.jonteohr.tejbz.PropertyHandler;
import com.github.jonteohr.tejbz.web.WebLog;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SetSchedule extends ListenerAdapter {
	public static void setSchedule(SlashCommandInteractionEvent e, InteractionHook hook) {
		PermissionHandler perms = new PermissionHandler();
		PropertyHandler props = new PropertyHandler();
		
		if(!perms.isAdmin(e.getMember()))
			return;
		
		if(e.getOptions().size() < 1) {
			hook.sendMessage("Incorrect usage.\nCorrect usage: `!setschedule <imageURL>`").queue();
			return;
		}
		
		try {
			URL url = new URL(e.getOption("image_url").getAsString());
			
			if(props.setProperty("schedule_url", url.toString())) {
				hook.sendMessage("Saved new schedule image!").queue();
				
				WebLog.addToWeblog("DISCORD", e.getUser().getAsTag(), "Updated the schedule: <a href='" + url + "'>New Schedule</a>");
				return;
			}
			
			hook.sendMessage("Error while saving. Try again later!").queue();
			return;
			
			
		} catch (MalformedURLException e1) {
			hook.sendMessage("Argument was not a valid image URL!").queue();
			System.out.println(e1);
			return;
		}
	}
}
