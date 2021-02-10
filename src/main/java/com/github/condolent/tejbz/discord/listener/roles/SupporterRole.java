package com.github.condolent.tejbz.discord.listener.roles;

import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SupporterRole extends ListenerAdapter {
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
		if(e.getRoles().contains(e.getGuild().getRoleById("278439657358753792")) || e.getRoles().contains(e.getGuild().getRoleById("640863929039323146"))) {
			e.getGuild().addRoleToMember(e.getMember(), e.getGuild().getRoleById("808968542007197707")).complete();
		}
	}

	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
		if(e.getRoles().contains(e.getGuild().getRoleById("278439657358753792")) || e.getRoles().contains(e.getGuild().getRoleById("640863929039323146"))) {
			e.getGuild().removeRoleFromMember(e.getMember(), e.getGuild().getRoleById("808968542007197707")).complete();
		}
	}
}
