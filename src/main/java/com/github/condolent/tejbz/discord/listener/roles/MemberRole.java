package com.github.condolent.tejbz.discord.listener.roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class MemberRole {
	public static Role memberRole;

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

			if(daysDiff >= 7 && monthsDiff < 3 && !member.getRoles().contains(memberRole)) {
				guild.addRoleToMember(member, memberRole).complete();
				guild.removeRoleFromMember(member, DefaultRoles.fresh).complete();
			}
		});

		System.out.println("Checked all members");
		checkedToday = true;
	}
}
