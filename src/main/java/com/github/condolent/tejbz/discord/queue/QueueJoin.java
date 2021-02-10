package com.github.condolent.tejbz.discord.queue;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class QueueJoin extends ListenerAdapter {

	public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
		// Supporter
		if(e.getChannelJoined().getId().equalsIgnoreCase("808995441428660254")) {
			WaitingQueue.queue.put(e.getMember().getId(), "Supporter");
			WaitingQueue.updateQueue();
		}

		// Regular
		else if(e.getChannelJoined().getId().equalsIgnoreCase("808994976431997030")) {
			WaitingQueue.queue.put(e.getMember().getId(), null);
			WaitingQueue.updateQueue();
		}
	}
}
