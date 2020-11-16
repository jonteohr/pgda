package com.github.condolent.tejbz;

import java.util.Date;

import javax.annotation.Nullable;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class Logging {
	
	/**
	 * Send a log to the log channel.
	 * @param msg
	 * @param logType
	 * @param embed
	 */
	public static void sendLog(@Nullable String msg, LogType logType, EmbedBuilder embed) {
		TextChannel channel = App.jda.getTextChannelById(App.logChannelId);
		Date date = new Date();
		embed.setColor(LogType.getColorFromType(logType));
		embed.setFooter(App.sdf.format(date));
		channel.sendMessage(embed.build()).queue();
	}
	
	/**
	 * Send a log to the log channel.
	 * @param msg
	 * @param logType
	 */
	public static void sendLog(String msg, LogType logType) {
		TextChannel channel = App.jda.getTextChannelById(App.logChannelId);
		Date date = new Date();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(LogType.getColorFromType(logType));
		eb.setAuthor("Action log");
		eb.setDescription(msg);
		eb.setFooter(App.sdf.format(date));
		
		channel.sendMessage(eb.build()).queue();
	}
	
	/**
	 * 
	 * @param title
	 * @param logType
	 * @param user
	 * @param mod
	 * @param reason
	 */
	public static void sendModLog(String title, LogType logType, String user, String mod, String reason) {
		TextChannel channel = App.jda.getTextChannelById(App.logChannelId);
		Date date = new Date();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(LogType.getColorFromType(logType));
		eb.setAuthor(title);
		eb.addField("User", user, true);
		eb.addField("Moderator", mod, true);
		eb.addField("Reason", reason, false);
		eb.setFooter(App.sdf.format(date));
		
		channel.sendMessage(eb.build()).queue();
	}
	
	/**
	 * 
	 * @param title
	 * @param logType
	 * @param user
	 * @param mod
	 */
	public static void sendModLog(String title, LogType logType, String user, String mod) {
		TextChannel channel = App.jda.getTextChannelById(App.logChannelId);
		Date date = new Date();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(LogType.getColorFromType(logType));
		eb.setAuthor(title);
		eb.addField("User", user, true);
		eb.addField("Moderator", mod, true);
		eb.setFooter(App.sdf.format(date));
		
		channel.sendMessage(eb.build()).queue();
	}
}
