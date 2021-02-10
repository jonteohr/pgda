package com.github.condolent.tejbz.discord.listener.roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class AutomaticRoles {
	public static Role memberRole;
	public static Role veteranRole;
	public static Role veteranDivider;
	public static Role threeMonths;
	public static Role sixMonths;
	public static Role twelveMonths;

	private static boolean checkedToday = false;

	public static void checkMemberStatus() {
		if(checkedToday)
			return;

		Guild guild = memberRole.getGuild();

		OffsetDateTime current = OffsetDateTime.now();

		guild.getMembers().forEach(member -> {
			System.out.println("Checking " + member.getUser().getAsTag());
			int daysDiff = new Long(ChronoUnit.DAYS.between(member.getTimeJoined(), current)).intValue();
			int monthsDiff = new Long(ChronoUnit.MONTHS.between(member.getTimeJoined(), current)).intValue();

			if((daysDiff >= 7 && monthsDiff < 3) && !member.getRoles().contains(memberRole)) {
				guild.addRoleToMember(member, memberRole).complete();
				guild.removeRoleFromMember(member, DefaultRoles.fresh).complete();
			} else if((monthsDiff >= 3 && monthsDiff < 6) && !member.getRoles().contains(veteranRole)) {
				guild.addRoleToMember(member, veteranRole).complete();
				guild.addRoleToMember(member, veteranDivider).complete();
				guild.removeRoleFromMember(member, memberRole).complete();
			}

			// Set veteran tiers
			if(monthsDiff >= 3 && monthsDiff < 6)
				guild.addRoleToMember(member, threeMonths).complete();
			else if(monthsDiff >= 6 && monthsDiff < 12) {
				guild.addRoleToMember(member, sixMonths).complete();
				guild.removeRoleFromMember(member, threeMonths).complete();
			} else if(monthsDiff >= 12) {
				guild.addRoleToMember(member, twelveMonths).complete();
				guild.removeRoleFromMember(member, sixMonths).complete();
			}
		});

		System.out.println("Checked all members");
		checkedToday = true;
	}
}
