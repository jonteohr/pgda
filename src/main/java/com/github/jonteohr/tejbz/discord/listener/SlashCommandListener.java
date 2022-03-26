package com.github.jonteohr.tejbz.discord.listener;

import com.github.jonteohr.tejbz.PermissionHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class SlashCommandListener extends ListenerAdapter {
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		if(e.getGuild() == null)
			return;

		switch(e.getName()) {
			case "test":
				doTest(e);
				break;
			default:
				e.reply("Didn't seem to work.. Try again later?").setEphemeral(true).queue();
				break;
		}
	}

	private void doTest(SlashCommandInteractionEvent e) {
		e.deferReply(true).queue();
		InteractionHook hook = e.getHook();
		hook.setEphemeral(true);

		PermissionHandler perms = new PermissionHandler();

		if(!perms.isAdmin(e.getMember())) {
			hook.sendMessage("Not admin..").queue();
			return;
		}

		hook.sendMessage("Seems to work!").queue();
	}
}
