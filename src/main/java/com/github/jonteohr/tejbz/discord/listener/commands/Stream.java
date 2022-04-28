package com.github.jonteohr.tejbz.discord.listener.commands;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.twitch.Twitch;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Stream {
	
	private static int cooldown = 0;

	public static void sendStream(SlashCommandInteractionEvent e, InteractionHook hook) {
		if(cooldown > 0) {
			hook.sendMessage("Timer: " + cooldown).queue();
			return;
		}

		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("Tejbz", null, Twitch.getUser("tejbz").getProfileImageUrl());
		msg.setColor(App.color);

		if(Twitch.getStream("tejbz") != null) {
			msg.setTitle(Twitch.getStream("tejbz").getTitle(), "https://twitch.tv/tejbz");

			msg.addField("Viewers", NumberFormat.getNumberInstance(Locale.US).format(Twitch.getStream("tejbz").getViewerCount()), true);
			msg.addField("Playing", Twitch.getGameById(Twitch.getStream("tejbz").getGameId()), true);
			msg.addField("Uptime", App.formatDuration(Twitch.getStream("tejbz").getUptime()), true);

			msg.setImage(Twitch.getStream("tejbz").getThumbnailUrl(1280,720));

			e.getChannel().sendMessageEmbeds(msg.build()).queue();

			cooldown = 30;
			cooldown();

			return;
		}

		msg.setDescription("Stream is offline.");

		hook.sendMessageEmbeds(msg.build()).queue();

		cooldown = 30;
		cooldown();
	}
	
	private static void cooldown() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(cooldown > 0)
					cooldown--;
			}
		}, 1000, 1000);
	}
}
