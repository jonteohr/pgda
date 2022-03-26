package com.github.jonteohr.tejbz.discord.listener.commands.admin;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.LogType;
import com.github.jonteohr.tejbz.Logging;
import com.github.jonteohr.tejbz.PermissionHandler;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Mute extends ListenerAdapter {
	public void onMessageReceived(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");
		
		if(!args[0].equalsIgnoreCase(App.prefix + "mute"))
			return;
		
		PermissionHandler perms = new PermissionHandler();
		
		if(!perms.isMod(e.getMember()))
			return;
		
		if(e.getMessage().getMentionedMembers().size() < 1) {
			e.getAuthor().openPrivateChannel().complete().sendMessage("No user specified.\nCorrect usage: `!mute <@user>`").queue();
			return;
		}
		
		Member target = e.getMessage().getMentionedMembers().get(0);
		Role role = e.getGuild().getRoleById("732558971000455198");
		
		// User is already muted
		if(target.getRoles().contains(role)) {
			e.getGuild().removeRoleFromMember(target, role).complete();
			Logging.sendModLog("User Un-muted", LogType.INFORMATION, target.getUser().getAsTag(), e.getAuthor().getAsTag());
			return;
		}
		
		e.getGuild().addRoleToMember(target, role).complete();
		Logging.sendModLog("User Muted", LogType.WARNING, target.getUser().getAsTag(), e.getAuthor().getAsTag());
		return;
	}
}
