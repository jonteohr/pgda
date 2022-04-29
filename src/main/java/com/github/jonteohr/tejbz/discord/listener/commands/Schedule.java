package com.github.jonteohr.tejbz.discord.listener.commands;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.PropertyHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Schedule {
	public static void sendSchedule(InteractionHook hook) {
		PropertyHandler prop = new PropertyHandler();

		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("PGDA", null, App.authorImage);
		msg.setColor(App.color);
		msg.setTitle("This weeks schedule");
		msg.setImage(prop.getPropertyValue("schedule_url"));

		hook.sendMessageEmbeds(msg.build()).queue();
	}
}
