package com.github.jonteohr.tejbz.discord.listener;

import com.github.jonteohr.tejbz.discord.listener.commands.Schedule;
import com.github.jonteohr.tejbz.discord.listener.commands.Social;
import com.github.jonteohr.tejbz.discord.listener.commands.Stream;
import com.github.jonteohr.tejbz.discord.listener.commands.Video;
import com.github.jonteohr.tejbz.discord.listener.commands.admin.ModHelp;
import com.github.jonteohr.tejbz.discord.listener.commands.admin.SetSchedule;
import com.github.jonteohr.tejbz.discord.listener.commands.admin.SetVideo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SlashCommandListener extends ListenerAdapter {
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if(e.getGuild() == null)
			return;

		switch(e.getName()) {
			case "social":
				doSocial(e);
				break;
			case "stream":
				doStream(e);
				break;
			case "schedule":
				doSchedule(e);
				break;
			case "youtube":
				doVideo(e);
				break;
			case "modhelp":
				doModHelp(e);
				break;
			case "setschedule":
				doSetSchedule(e);
				break;
			case "setvideo":
				doSetVideo(e);
				break;
			default:
				e.reply("Didn't seem to work.. Try again later?").setEphemeral(true).queue();
				break;
		}
	}

	private void doSocial(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		Social.sendSocial(hook);
	}

	private void doStream(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		Stream.sendStream(e, hook);
	}

	private void doSchedule(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		Schedule.sendSchedule(hook);
	}

	private void doVideo(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		Video.sendVideo(e, hook);
	}

	private void doModHelp(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		ModHelp.sendModHelp(e, hook);
	}

	private void doSetSchedule(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		SetSchedule.setSchedule(e, hook);
	}

	private void doSetVideo(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		SetVideo.setVideo(e, hook);
	}
}
