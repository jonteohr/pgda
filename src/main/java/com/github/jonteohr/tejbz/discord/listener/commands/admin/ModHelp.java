package com.github.jonteohr.tejbz.discord.listener.commands.admin;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.PermissionHandler;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class ModHelp extends ListenerAdapter {
	public static void sendModHelp(SlashCommandInteractionEvent e, InteractionHook hook) {
		PermissionHandler perms = new PermissionHandler();
		
		if(!perms.isMod(e.getMember()))
			return;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("PGDA", null, App.authorImage);
		msg.setColor(App.color);
		msg.setTitle("Mod/Admin commands");
		msg.setDescription("Confirmation messages are sent out in PMs. If you don't get one, you don't have access!\n"
				+ "I'd recommend using these commands in " + e.getGuild().getTextChannelById("489590000556441603").getAsMention() + " since nobody else needs to see this.");
		
		msg.addField("Command", ""
				+ "`!setschedule <imageURL>`\n"
				+ "`!setvideo <videoURL>`\n"
				+ "`!mute <@user>`\n"
				+ "`!togglejoin`\n"
				+ "`!queuelimit <number>`", true);
		msg.addField("Description", ""
				+ "Sets the new image of `!schedule`.\n"
				+ "Sets the link as the most recent video.\n"
				+ "Mutes/Unmutes a mentioned member\n"
				+ "Toggles the queue system.\n"
				+ "Sets the max limit of users in the Live lobby. If there's less people in channel than set amount and joining is enabled then the next person in queue will join.", true);
		
		hook.sendMessageEmbeds(msg.build()).queue();
	}
}
