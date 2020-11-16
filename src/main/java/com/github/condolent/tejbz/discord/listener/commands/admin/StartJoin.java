package com.github.condolent.tejbz.discord.listener.commands.admin;

import com.github.condolent.tejbz.App;
import com.github.condolent.tejbz.PermissionHandler;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StartJoin extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		PermissionHandler perms = new PermissionHandler();
		
		if(args[0].equalsIgnoreCase(App.prefix + "togglejoin")) {
			
			if(!perms.isMod(e.getMember()))
				return;
			
			if(App.enableJoin) {
				App.general.sendMessage("The queue system is now **disabled**. Thanks for participating!").queue();
				App.enableJoin = false;
				return;
			}
			
			App.general.sendMessage("The queue system is now **active**! Type `!join` to join the queue to play with Tejbz.").queue();
			App.enableJoin = true;
		}
		
		if(args[0].equalsIgnoreCase(App.prefix + "queuelimit")) {
			if(!perms.isMod(e.getMember()))
				return;
			
			if(args.length < 2) {
				e.getChannel().sendMessage("Current limit is set to: " + App.joinLimit).queue();
				return;
			}
			
			if(isInteger(args[1])) {
				App.joinLimit = Integer.parseInt(args[1]);
				e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Set the new queue limit to " + App.joinLimit).queue();
				return;
			}
			
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Argument is not a number.").queue();
		}
	}
	
	private static boolean isInteger(String s) {
	    try {
	    	Integer.parseInt(s);
	    } catch (Exception e) {
			return false;
		}
	    
	    return true;
	}
}
