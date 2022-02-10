package com.github.jonteohr.tejbz.discord;

import com.github.jonteohr.tejbz.App;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ServerStats {
	public static VoiceChannel statChannel;

	public static void updateMemberCount() {
		statChannel.getManager().setName("Members: " + App.guild.getMembers().size()).queue();
	}
}
