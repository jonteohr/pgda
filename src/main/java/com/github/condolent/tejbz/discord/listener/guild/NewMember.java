package com.github.condolent.tejbz.discord.listener.guild;

import com.github.condolent.tejbz.discord.listener.roles.DefaultRoles;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewMember extends ListenerAdapter {
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		if(!e.getMember().getUser().isBot())
			DefaultRoles.grantDefaultRoles(e.getMember(), e.getGuild());
	}
}
