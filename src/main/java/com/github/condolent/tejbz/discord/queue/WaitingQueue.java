package com.github.condolent.tejbz.discord.queue;

import com.github.condolent.tejbz.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;

public class WaitingQueue {
	public static VoiceChannel waitingRoom;
	public static VoiceChannel supporterRoom;
	public static TextChannel infoChannel;

	public static Message queueMessage;

	public static List<String> priorityQueue = new ArrayList<>();
	public static List<String> queue = new ArrayList<>();
	public static Map<String, Integer> priorityExpires = new HashMap<>();
	public static Map<String, Integer> expires = new HashMap<>();

	public static void editInfo() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(App.color);
		eb.setTitle("__How do I join the community games?__");
		eb.setDescription("**Joining the queue**\n" +
				"Join the [⏰ Waiting Room](https://discord.gg/npjV9zEdS3) if you are a viewer.\n" +
				"Join the [⚡ Supporter Waiting Room](https://discord.gg/Zegejjf7Ce/) if you are a supporter/subscriber.\n\n" +
				"**Getting in the voice call**\n" +
				"__After__ you've joined the queue, you will be given a queue number below. Supporters are given priority access.\n" +
				"Once a spot opens up, a " + infoChannel.getGuild().getRoleById("809110470602260555").getAsMention() + " will move the first person in queue to the voice call.");

		infoChannel.retrieveMessageById("809002784552779796").complete().editMessage(eb.build()).queue();
	}

	public static void updateQueue(Guild guild) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(App.color);
		eb.setTitle("__Queue__");

		StringBuilder prio = new StringBuilder();
		if(priorityQueue.size() < 1) {
			prio.setLength(0);
			prio.append("_Nobody in queue..._");
		}
		for(int i = 0; i < priorityQueue.size(); i++) {
			prio.append(
				(i + 1) + ". " + guild.getMemberById(priorityQueue.get(i)).getAsMention() +
				(priorityExpires.containsKey(priorityQueue.get(i)) ? " (Expires in < 2 minutes)" : "") +
				"\n"
			);
		}

		StringBuilder regular = new StringBuilder();
		if(queue.size() < 1) {
			regular.setLength(0);
			regular.append("_Nobody in queue..._");
		}
		for(int i = 0; i < queue.size(); i++) {
			regular.append(
				(i + 1 + priorityQueue.size()) + ". " + guild.getMemberById(queue.get(i)).getAsMention() +
				(expires.containsKey(queue.get(i)) ? " (Expires in < 2 minutes)" : "") +
				"\n"
			);
		}

		eb.setDescription("Please be patient and wait until one of the spots in the voice call opens up.\n\n" +
				"**Supporter waiting queue**\n" +
				prio +
				"\n\n**Waiting queue**\n" +
				regular +
				"");

		queueMessage.editMessage(eb.build()).queue();
	}

	public static void expirationTimer() {
		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Prio expires: " + priorityExpires);
				System.out.println("Regular expires: " + expires);
				if(priorityQueue.size() < 1 && queue.size() < 1)
					return;

				List<String> removes = new ArrayList<>();
				List<String> removesPrio = new ArrayList<>();

				expires.forEach((k,v) -> {
					expires.replace(k, (v - 1));

					if(expires.get(k) < 1)
						removes.add(k);
				});
				priorityExpires.forEach((k,v) -> {
					priorityExpires.replace(k, (v - 1));

					if(priorityExpires.get(k) < 1)
						removesPrio.add(k);
				});

				removes.forEach(WaitingQueue::kickFromQueue);
				removesPrio.forEach(WaitingQueue::kickFromPrio);
				updateQueue(infoChannel.getGuild());
			}
		}, 0, 60*1000);
	}

	private static void kickFromQueue(String userId) {
		expires.remove(userId);
		queue.remove(userId);
	}

	private static void kickFromPrio(String userId) {
		priorityExpires.remove(userId);
		priorityQueue.remove(userId);
	}
}
