package com.jonteohr.tejbz;

import net.dv8tion.jda.api.entities.Member;

public class PermissionHandler {
	public boolean isAdmin(Member member) {
		return member.getRoles().contains(member.getGuild().getRoleById("124204787628507136"));
	}
	
	public boolean isMod(Member member) {
		return isAdmin(member) || member.getRoles().contains(member.getGuild().getRoleById("124204592941629442"));
	}
	
	public boolean isSub(Member member) {
		if(member.getRoles().contains(member.getGuild().getRoleById("278439657358753792"))) // Twitch sub
			return true;

		// Youtube member
		return member.getRoles().contains(member.getGuild().getRoleById("640863929039323146"));
	}
}
