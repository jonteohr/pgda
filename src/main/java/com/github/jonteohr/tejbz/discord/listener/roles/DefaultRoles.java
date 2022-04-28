package com.github.jonteohr.tejbz.discord.listener.roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class DefaultRoles {

	public static Role fresh;

	// Dividers
	public static Role supporterDivider;
	public static Role tierDivider;
	public static Role titleDivider;
	public static Role gameDivider;
	public static Role otherDivider;

	public static void setRoles(Guild guild) {
		fresh = guild.getRoleById("808782735078064129");
		supporterDivider = guild.getRoleById("808785052845998091");
		tierDivider = guild.getRoleById("808780925080043540");
		titleDivider = guild.getRoleById("809156901127454802");
		gameDivider = guild.getRoleById("808793052452880434");
		otherDivider = guild.getRoleById("808787548814049371");
	}

	public static void grantDefaultRoles(Member member, Guild guild) {
		guild.addRoleToMember(member, fresh).complete();
		guild.addRoleToMember(member, titleDivider).complete();
		guild.addRoleToMember(member, gameDivider).complete();
		guild.addRoleToMember(member, otherDivider).complete();
	}

	public static void loopAllMembers(Guild guild) {
		System.out.println("Starting too loop and giving roles");
		System.out.println("Going through " + guild.getMembers().size() + " members.");
		guild.getMembers().forEach(member -> {
			if(member.getUser().isBot()) // Skip bots
				return;

			System.out.println("Checking " + member.getUser().getAsTag());
			if(!member.getRoles().contains(guild.getRoleById("808968542007197707"))) {
				guild.removeRoleFromMember(member, supporterDivider).complete();
				guild.removeRoleFromMember(member, tierDivider).complete();
			}

			// Check supporter roles
			if(member.getRoles().contains(guild.getRoleById("278439657358753792")) || member.getRoles().contains(guild.getRoleById("640863929039323146"))) {
				guild.addRoleToMember(member, supporterDivider).complete();
				guild.addRoleToMember(member, tierDivider).complete();
				guild.addRoleToMember(member, guild.getRoleById("808968542007197707")).complete();
			}

			// Dividers
			if(!member.getRoles().contains(gameDivider))
				guild.addRoleToMember(member, gameDivider).complete();
			if(!member.getRoles().contains(otherDivider))
				guild.addRoleToMember(member, otherDivider).complete();
			if(!member.getRoles().contains(titleDivider))
				guild.addRoleToMember(member, titleDivider).complete();
		});

		System.out.println("Loop finished! All setup");
	}
}
