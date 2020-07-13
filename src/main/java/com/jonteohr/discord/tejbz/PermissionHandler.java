package com.jonteohr.discord.tejbz;

import net.dv8tion.jda.api.entities.Member;

public class PermissionHandler {
	public boolean isAdmin(Member member) {
		if(member.getRoles().contains(member.getGuild().getRoleById("699677272516460564")))
			return true;
		
		return false;
	}
	
	public boolean isMod(Member member) {
		if(isAdmin(member) || member.getRoles().contains(member.getGuild().getRoleById("699677272516460564")))
			return true;
		
		return false;
	}
}
