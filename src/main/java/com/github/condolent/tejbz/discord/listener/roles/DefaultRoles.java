package com.github.condolent.tejbz.discord.listener.roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class DefaultRoles {
	public static Role fresh;

	// Dividers
	public static Role supporterDivider;
	public static Role tierDivider;
	public static Role gameDivider;
	public static Role otherDivider;

	public static void setRoles(Guild guild) {
		fresh = guild.getRoleById("808782735078064129");
		supporterDivider = guild.getRoleById("808785052845998091");
		tierDivider = guild.getRoleById("808780925080043540");
		gameDivider = guild.getRoleById("808793052452880434");
		otherDivider = guild.getRoleById("808787548814049371");
	}

	public static void grantDefaultRoles(Member member, Guild guild) {
		guild.addRoleToMember(member, fresh).complete();
		guild.addRoleToMember(member, supporterDivider).complete();
		guild.addRoleToMember(member, tierDivider).complete();
		guild.addRoleToMember(member, gameDivider).complete();
		guild.addRoleToMember(member, otherDivider).complete();
	}

	public static void rewardCurrentMembers(Guild guild) {
		System.out.println("Starting too loop and giving roles");
		System.out.println("Going through " + guild.getMembers().size() + " members.");
		guild.getMembers().forEach(member -> {
			// Dividers
			if(!member.getRoles().contains(DefaultRoles.supporterDivider))
				guild.addRoleToMember(member, DefaultRoles.supporterDivider).complete();
			if(!member.getRoles().contains(DefaultRoles.gameDivider))
				guild.addRoleToMember(member, DefaultRoles.gameDivider).complete();
			if(!member.getRoles().contains(DefaultRoles.otherDivider))
				guild.addRoleToMember(member, DefaultRoles.otherDivider).complete();
			if(!member.getRoles().contains(DefaultRoles.tierDivider))
				guild.addRoleToMember(member, DefaultRoles.tierDivider).complete();

			if(
					!member.getRoles().contains(DefaultRoles.fresh) &&
							!member.getRoles().contains(guild.getRoleById("124204787628507136")) && // Admin role
							!member.getRoles().contains(guild.getRoleById("280730890945036288")) && // PGDA Boys role
							!member.getRoles().contains(guild.getRoleById("124204592941629442")) && // Twitch Moderator role
							!member.getRoles().contains(guild.getRoleById("794945930809966642")) && // MC Staff role
							!member.getRoles().contains(guild.getRoleById("319560778267230209")) // Team role
			)
				guild.addRoleToMember(member, DefaultRoles.fresh).complete();
		});

		System.out.println("Loop finished! All setup");
	}
}
