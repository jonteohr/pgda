package com.github.jonteohr.tejbz.discord.listener.commands.admin;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.PermissionHandler;
import com.github.jonteohr.tejbz.PropertyHandler;
import com.github.jonteohr.tejbz.web.WebLog;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SetSchedule extends ListenerAdapter {
	public void onMessageReceived(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "setschedule"))
			return;
		
		PermissionHandler perms = new PermissionHandler();
		PropertyHandler props = new PropertyHandler();
		
		if(!perms.isAdmin(e.getMember()))
			return;
		
		if(args.length < 2) {
			e.getAuthor().openPrivateChannel().complete().sendMessage("Incorrect usage.\nCorrect usage: `!setschedule <imageURL>`").queue();
			return;
		}
		
		try {
			URL url = new URL(args[1]);
			
			if(props.setProperty("schedule_url", url.toString())) {
				e.getAuthor().openPrivateChannel().complete().sendMessage("Saved new schedule image!").queue();
				
				WebLog.addToWeblog("DISCORD", e.getAuthor().getAsTag(), "Updated the schedule: <a href='" + url.toString() + "'>New Schedule</a>");
				return;
			}
			
			e.getAuthor().openPrivateChannel().complete().sendMessage("Error while saving. Try again later!").queue();
			return;
			
			
		} catch (MalformedURLException e1) {
			e.getAuthor().openPrivateChannel().complete().sendMessage("Argument was not a valid image URL!").queue();
			System.out.println(e1);
			return;
		}
	}
}
