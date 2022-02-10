package com.github.jonteohr.tejbz.discord.listener.guild;

import com.github.jonteohr.tejbz.discord.ServerStats;
import com.github.jonteohr.tejbz.discord.listener.roles.DefaultRoles;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewMember extends ListenerAdapter {
	public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		if(!e.getMember().getUser().isBot())
			DefaultRoles.grantDefaultRoles(e.getMember(), e.getGuild());

		ServerStats.updateMemberCount();
	}

	public void onGuildMemberRemove(GuildMemberRemoveEvent e) {
		ServerStats.updateMemberCount();
	}
}
