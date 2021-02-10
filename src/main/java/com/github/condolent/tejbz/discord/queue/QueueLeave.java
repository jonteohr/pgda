package com.github.condolent.tejbz.discord.queue;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class QueueLeave extends ListenerAdapter {

	public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
		// Supporter
		if(e.getChannelLeft().getId().equalsIgnoreCase("808995441428660254")) {
			/*
				TODO on timer, after 2 minutes kick from list
			 */
			WaitingQueue.queue.remove(e.getMember().getId());
			WaitingQueue.updateQueue();
		}

		// Regular
		else if(e.getChannelLeft().getId().equalsIgnoreCase("808994976431997030")) {
			/*
				TODO on timer, after 2 minutes kick from list
			 */
			WaitingQueue.queue.remove(e.getMember().getId());
			WaitingQueue.updateQueue();
		}
	}
}
