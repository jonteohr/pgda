package com.jonteohr.discord.tejbz.listener.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.PermissionHandler;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Join extends ListenerAdapter {
	
	private static List<String> placement = new ArrayList<String>();
	
	public static VoiceChannel lobby;
	public static VoiceChannel queue; 
	public static VoiceChannel live;
	
	public void onGuildReady(GuildReadyEvent e) {
		lobby = App.guild.getVoiceChannelById("124204246815080449");
		queue = App.guild.getVoiceChannelById("732569438326489139");
		live = App.guild.getVoiceChannelById("280730003484835860");
		
		ticketTimer();
	}
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "join"))
			return;
		
		PermissionHandler perms = new PermissionHandler();
		
		if(!perms.isSub(e.getMember())) {
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " You must be a subscriber to join!").queue();
			return;
		}
		
		if(!App.enableJoin) {
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " Joining is currently disabled.").queue();
			return;
		}
		
		if(!e.getMember().getVoiceState().inVoiceChannel() || !e.getMember().getVoiceState().getChannel().equals(lobby)) {
			e.getChannel().sendMessage(e.getAuthor().getAsMention() + " You must be connected to the Lobby channel while performing this command.").queue();
			return;
		}
		
		int ticket = queue.getMembers().size() + 1;
		placement.add(queue.getMembers().size(), e.getAuthor().getId());
		
		e.getGuild().moveVoiceMember(e.getMember(), queue).complete();
		e.getAuthor().openPrivateChannel().complete().sendMessage("You've been placed in queue. Make sure you do not leave this lobby or else you will lose your spot!").queue();
		e.getAuthor().openPrivateChannel().complete().sendMessage("Your ticket number is: " + ticket).queue();
	}
	
	public static void ticketTimer() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(!App.enableJoin)
					return;
				
				if(queue.getMembers().size() < App.joinLimit) {
					if(placement.size() < 1)
						return;
					
					App.guild.moveVoiceMember(App.guild.getMemberById(placement.get(0)), live).complete();
					
					System.out.println("Current queue: " + placement);
					
					List<String> old = placement;
					placement.clear();
					
					for(int i = 0; i < old.size(); i++) {
						placement.add(i - 1, old.get(i));
						
						if(i == 0)
							continue;
						App.jda.getUserById(old.get(i)).openPrivateChannel().complete().sendMessage("Your new ticket placement is: " + (i - 1)).queue();
					}
					
					System.out.println("New queue: " + placement);
					
					return;
				}
			}
		}, 5*1000, 5*1000);
	}
}
