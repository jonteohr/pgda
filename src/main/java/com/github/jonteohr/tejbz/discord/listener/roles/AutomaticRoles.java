package com.github.jonteohr.tejbz.discord.listener.roles;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class AutomaticRoles {
	public static Role memberRole;
	public static Role veteranRole;
	public static Role veteranDivider;
	public static Role twelveMonths;
	public static Role eighteenMonths;
	public static Role twentyfourMonths;

	private static boolean checkedToday = false;

	public static void checkMemberStatus() {
		if(checkedToday)
			return;

		Guild guild = memberRole.getGuild();
		OffsetDateTime current = OffsetDateTime.now();

		System.out.println("Checking member status...");
		guild.getMembers().forEach(member -> {
			if(member.getUser().isBot())
				return;

			int daysDiff = new Long(ChronoUnit.DAYS.between(member.getTimeJoined(), current)).intValue();
			int monthsDiff = new Long(ChronoUnit.MONTHS.between(member.getTimeJoined(), current)).intValue();

			if((daysDiff >= 7 && monthsDiff < 12) && !member.getRoles().contains(memberRole)) {
				guild.addRoleToMember(member, memberRole).complete();
				guild.removeRoleFromMember(member, DefaultRoles.fresh).complete();
			} else if(monthsDiff >= 12 && !member.getRoles().contains(veteranRole)) {
				guild.addRoleToMember(member, veteranRole).complete();
				guild.addRoleToMember(member, veteranDivider).complete();
				guild.removeRoleFromMember(member, memberRole).complete();

				// Set veteran tiers
				if(monthsDiff >= 12 && monthsDiff < 18) {
					guild.addRoleToMember(member, twelveMonths).complete();
				} else if(monthsDiff >= 18 && monthsDiff < 24) {
					guild.addRoleToMember(member, eighteenMonths).complete();
					guild.removeRoleFromMember(member, twelveMonths).complete();
				} else if(monthsDiff >= 24) {
					guild.addRoleToMember(member, twentyfourMonths).complete();
					guild.removeRoleFromMember(member, eighteenMonths).complete();
				}
			}
		});

		System.out.println("Checked all members");
		checkedToday = true;
	}
}
