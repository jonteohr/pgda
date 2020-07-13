package com.jonteohr.discord.tejbz.twitch;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.common.enums.SubscriptionType;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.pubsub.events.ChannelBitsEvent;
import com.github.twitch4j.pubsub.events.ChannelSubscribeEvent;
import com.jonteohr.discord.tejbz.App;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class TwitchHandler {
	
	@EventSubscriber
	public void onTwitchChat(IRCMessageEvent e) {
		if(!e.getChannel().getName().equalsIgnoreCase("rlhypr"))
			return;
		
		if(e.getMessage().isEmpty())
			return;
		
		if(e.getMessage().get().equalsIgnoreCase("!command")) {
			Twitch.chatClient.getChat().sendMessage("rlhypr", "@" + e.getUserName() + " Replyyy");
		}
	}
	
	@EventSubscriber
	public void onLive(ChannelGoLiveEvent e) {
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("Tejbz", null, Twitch.getUser(e.getChannel().getName()).getProfileImageUrl());
		msg.setColor(App.color);
		msg.setThumbnail(Twitch.getStream("tejbz").getThumbnailUrl());
		msg.setTitle("Tejbz just went live!", "https://www.twitch.tv/tejbz");
		msg.addField("Title", e.getStream().getTitle(), false);
		msg.addField("Playing", Twitch.getGameById(e.getStream().getGameId()), false);
		
		TextChannel channel = App.jda.getGuildById("699675742727831593").getTextChannelById("699678530715254956");
		channel.sendMessage(msg.build()).queue();
		
		System.out.println("Tejbz went live.");
	}
	
	@EventSubscriber
	public void onSub(ChannelSubscribeEvent e) {
		System.out.println("Sub fired");
		TextChannel channel = App.jda.getGuilds().get(0).getTextChannelById("699678530715254956");
		String message = (e.getData().getSubMessage().getMessage().isEmpty() ? null : e.getData().getSubMessage().getMessage());
		String tier = e.getData().getSubPlanName();
		
		String user = e.getData().getChannelName();
		String months = e.getData().getCumulativeMonths().toString();
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(App.color);
		msg.setAuthor("PGDA", null, App.authorImage);
		
		if(e.getData().getContext().equals(SubscriptionType.SUB_GIFT)) {
			msg.setDescription("[" + e.getData().getDisplayName() + "](https://twitch.tv/" + e.getData().getRecipientDisplayName() + ") gifted a sub to [" + user + "](https://twitch.tv/" + user + ")");
		} else {
			msg.setDescription("[" + user + "](https://twitch.tv/" + user + ") subscribed with " + tier + ". They've been subscribed for " + months + " months.");
			if(message != null)
				msg.addField("Message", message, true);
			if(e.getData().getStreakMonths() > 0)
				msg.addField("Streak", e.getData().getStreakMonths() + " months", true);
		}
		
		channel.sendMessage(msg.build()).queue();
		
		/*
		 * Twitch Chat
		 */
		if(Twitch.chatClient.getChat().isChannelJoined("rlhypr"))
			Twitch.chatClient.getChat().sendMessage("rlhypr", "/me tejbzWave Welcome to the squad, @" + user + " tejbzLove");
	}
	
	@EventSubscriber
	public void onCheer(ChannelBitsEvent e) {
		TextChannel channel = App.jda.getGuilds().get(0).getTextChannelById("699678530715254956");
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(0x7823EE);
		msg.setAuthor("PGDA", null, "https://dev.twitch.tv/marketing-assets/images/bits-in-extensions/diamond.png");
		msg.setDescription("[" + e.getData().getUserName() + "](https://twitch.tv/" + e.getData().getUserName() + ") cheered " + e.getData().getBitsUsed() + " bits.");
		if(!e.getData().getChatMessage().isEmpty())
			msg.addField("Message", e.getData().getChatMessage(), true);
		
		channel.sendMessage(msg.build()).queue();
		
		/*
		 * Twitch Chat
		 */
		if(Twitch.chatClient.getChat().isChannelJoined("rlhypr"))
			Twitch.chatClient.getChat().sendMessage("rlhypr", "/me Thanks for the cheer, @" + e.getData().getUserName() + " tejbzLove");
	}
	
}
