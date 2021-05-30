package com.github.jonteohr.tejbz.discord.queue;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelEvent extends ListenerAdapter {

	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		if(e.getMember().getUser().isBot())
			return;

		// Supporter
		if(e.getChannelJoined().getId().equalsIgnoreCase("808995441428660254"))
			joinQueue(e.getGuild(), e.getMember().getId(), true);

		// Regular
		else if(e.getChannelJoined().getId().equalsIgnoreCase("808994976431997030"))
			joinQueue(e.getGuild(), e.getMember().getId(), false);
	}

	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		if(e.getMember().getUser().isBot())
			return;

		// Supporter
		if(e.getChannelLeft().getId().equalsIgnoreCase("808995441428660254"))
			leaveQueue(e.getGuild(), e.getMember().getId(), true);

		// Regular
		else if(e.getChannelLeft().getId().equalsIgnoreCase("808994976431997030"))
			leaveQueue(e.getGuild(), e.getMember().getId(), false);
	}

	public void onGuildVoiceMove(GuildVoiceMoveEvent e) {
		if(e.getMember().getUser().isBot())
			return;

		String userId = e.getMember().getId();

		if(e.getChannelJoined().getId().equalsIgnoreCase("808995441428660254"))
			joinQueue(e.getGuild(), userId, true);
		else if(e.getChannelJoined().getId().equalsIgnoreCase("808994976431997030"))
			joinQueue(e.getGuild(), userId, false);

		if(e.getChannelLeft().getId().equalsIgnoreCase("808995441428660254"))
			leaveQueue(e.getGuild(), userId, true);
		else if(e.getChannelLeft().getId().equalsIgnoreCase("808994976431997030"))
			leaveQueue(e.getGuild(), userId, false);
	}

	private void joinQueue(Guild guild, String userId, boolean supporter) {
		// Supporter
		if(supporter) {
			if(WaitingQueue.priorityExpires.containsKey(userId)) {
				WaitingQueue.priorityExpires.remove(userId);
			} else {
				WaitingQueue.priorityQueue.add(userId);
			}

			WaitingQueue.updateQueue(guild);
		}

		// Regular
		else {
			if(WaitingQueue.expires.containsKey(userId))
				WaitingQueue.expires.remove(userId);
			else
				WaitingQueue.queue.add(userId);

			WaitingQueue.updateQueue(guild);
		}
	}

	private void leaveQueue(Guild guild, String userId, boolean supporter) {
		if(supporter) {
			WaitingQueue.priorityExpires.put(userId, 2);
			WaitingQueue.updateQueue(guild);
		} else {
			WaitingQueue.expires.put(userId, 2);
			WaitingQueue.updateQueue(guild);
		}
	}
}