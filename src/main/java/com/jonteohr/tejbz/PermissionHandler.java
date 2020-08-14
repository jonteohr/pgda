package com.jonteohr.tejbz;

import net.dv8tion.jda.api.entities.Member;

public class PermissionHandler {
	public boolean isAdmin(Member member) {
		if(member.getRoles().contains(member.getGuild().getRoleById("124204787628507136")))
			return true;
		
		return false;
	}
	
	public boolean isMod(Member member) {
		if(isAdmin(member) || member.getRoles().contains(member.getGuild().getRoleById("124204592941629442")))
			return true;
		
		return false;
	}
	
	public boolean isSub(Member member) {
		if(member.getRoles().contains(member.getGuild().getRoleById("278439657358753792"))) // Twitch sub
			return true;
		
		if(member.getRoles().contains(member.getGuild().getRoleById("640863929039323146"))) // Youtube member
			return true;
		
		return false;
	}
}
