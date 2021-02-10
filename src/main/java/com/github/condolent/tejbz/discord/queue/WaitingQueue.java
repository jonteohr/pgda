package com.github.condolent.tejbz.discord.queue;

import com.github.condolent.tejbz.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class WaitingQueue {
	public static VoiceChannel waitingRoom;
	public static VoiceChannel supporterRoom;
	public static TextChannel infoChannel;

	public static Message queueMessage;

	public static void editInfo() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(App.color);
		eb.setTitle("__How do I join the community games?__");
		eb.setDescription("**Joining the queue**\n" +
				"Join the [⏰ Waiting Room](https://discord.gg/npjV9zEdS3) if you are a viewer.\n" +
				"Join the [⚡ Supporter Waiting Room](https://discord.gg/Zegejjf7Ce/) if you are a supporter/subscriber.\n\n" +
				"**Getting in the voice call**\n" +
				"__After__ you've joined the queue, you will be given a queue number below. Supporters are given priority access.\n" +
				"Once a spot opens up, a " + infoChannel.getGuild().getRoleById("124204592941629442").getAsMention() + " will move the first person in queue to the voice call.");

//		infoChannel.sendMessage(eb.build()).queue();
	}

	public static void editQueue() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(App.color);
		eb.setTitle("__Queue__");
		eb.setDescription("Please be patient and wait until one of the spots in the voice call opens up.\n\n" +
				"**Supporter waiting queue**\n\n" +
				"**Waiting queue**");

		queueMessage.editMessage(eb.build()).queue();
	}
}
