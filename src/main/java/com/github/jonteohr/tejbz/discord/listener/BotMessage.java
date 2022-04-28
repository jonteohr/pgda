package com.github.jonteohr.tejbz.discord.listener;

import com.github.jonteohr.tejbz.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotMessage extends ListenerAdapter {

	private TextChannel publishChannel;

	public void onMessageReceived(MessageReceivedEvent e) {
		if(!e.getChannel().getId().equalsIgnoreCase("809704199142375474"))
			return;

		if(e.getAuthor().isBot())
			return;

		String[] message = e.getMessage().getContentRaw().split(" ", 2);

		if(message.length < 2)
			return;

		if(e.getMessage().getMentionedChannels().size() < 1)
			return;

		publishChannel = e.getMessage().getMentionedChannels().get(0);

		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(App.color);
		eb.setAuthor(e.getAuthor().getName(), null, e.getAuthor().getAvatarUrl());
		eb.setDescription(message[1]);

		e.getChannel().sendMessageEmbeds(eb.build()).queue(success -> success.addReaction("✅").queue());
	}

	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if(!e.getChannel().getId().equalsIgnoreCase("809704199142375474"))
			return;
		if(e.getUser().isBot())
			return;

		Message originalMessage = e.getChannel().retrieveMessageById(e.getMessageId()).complete();

		if(!originalMessage.getAuthor().isBot())
			return;

		if(e.getReactionEmote().getAsReactionCode().equalsIgnoreCase("✅")) {
			publishChannel.sendMessageEmbeds(originalMessage.getEmbeds().get(0)).queue();
			originalMessage.delete().complete();
		}
	}
}
