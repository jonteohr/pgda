/*
 * https://discord.com/api/oauth2/authorize?client_id=731137216264536124&permissions=500694208&scope=bot
 */

package com.jonteohr.discord.tejbz;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;

import javax.security.auth.login.LoginException;

import com.jonteohr.discord.tejbz.credentials.Credentials;
import com.jonteohr.discord.tejbz.listener.VideoAnnouncer;
import com.jonteohr.discord.tejbz.listener.commands.Follow;
import com.jonteohr.discord.tejbz.listener.commands.Schedule;
import com.jonteohr.discord.tejbz.listener.commands.Social;
import com.jonteohr.discord.tejbz.listener.commands.Stream;
import com.jonteohr.discord.tejbz.listener.commands.Video;
import com.jonteohr.discord.tejbz.listener.commands.admin.SetSchedule;
import com.jonteohr.discord.tejbz.listener.commands.admin.SetVideo;
import com.jonteohr.discord.tejbz.listener.guild.GuildReady;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class App {
	public static JDA jda;
	
	public static String prefix = "!";
	public static int color = 0x39A0FE;
	public static String authorImage = "https://static-cdn.jtvnw.net/jtv_user_pictures/7f35ded7-e1d7-4cb3-9a46-a47e8ff56e3a-profile_image-300x300.png";
	
	public static void main(String[] args) throws LoginException {
		Collection<GatewayIntent> intents = new ArrayList<GatewayIntent>();
		intents.addAll(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
		
		jda = JDABuilder.create(Credentials.TOKEN.getValue(), intents)
				.build();
		
		jda.addEventListener(new GuildReady());
		
		// Commands
		jda.addEventListener(new Social());
		jda.addEventListener(new Follow());
		jda.addEventListener(new Stream());
		jda.addEventListener(new Video());
		jda.addEventListener(new Schedule());
		
		// Admin commands
		jda.addEventListener(new SetVideo());
		jda.addEventListener(new SetSchedule());
		
		// Misc
		jda.addEventListener(new VideoAnnouncer());
		
		VideoAnnouncer.videoTimer();
	}
	
	public static String formatDuration(Duration duration) {
	    long seconds = duration.getSeconds();
	    long absSeconds = Math.abs(seconds);
	    String positive = String.format(
	        "%dh %02dm %02ds",
	        absSeconds / 3600,
	        (absSeconds % 3600) / 60,
	        absSeconds % 60);
	    return seconds < 0 ? "-" + positive : positive;
	}

}
