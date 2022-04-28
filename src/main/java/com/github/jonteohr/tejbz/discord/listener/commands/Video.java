package com.github.jonteohr.tejbz.discord.listener.commands;

import com.github.jonteohr.tejbz.PropertyHandler;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Video {

	public static void sendVideo(SlashCommandInteractionEvent e, InteractionHook hook) {
		PropertyHandler prop = new PropertyHandler();

		String url = prop.getPropertyValue("recent_video");

		hook.sendMessage(e.getMember().getAsMention() + " Check out the latest video on the YouTube channel!\n" + url).queue();
	}
	
	public static void sendAd(TextChannel channel) {
		PropertyHandler prop = new PropertyHandler();
		
		String url = prop.getPropertyValue("recent_video");
		
		channel.sendMessage("Check out the latest video on the YouTube channel!\n" + url).queue();
	}
}
