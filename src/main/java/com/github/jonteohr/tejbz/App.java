/*
 * https://discord.com/api/oauth2/authorize?client_id=731137216264536124&permissions=500694208&scope=bot
 */

package com.github.jonteohr.tejbz;

import com.github.jonteohr.tejbz.credentials.Credentials;
import com.github.jonteohr.tejbz.credentials.Identity;
import com.github.jonteohr.tejbz.discord.listener.BotMessage;
import com.github.jonteohr.tejbz.discord.listener.commands.*;
import com.github.jonteohr.tejbz.discord.listener.VideoAnnouncer;
import com.github.jonteohr.tejbz.discord.listener.commands.admin.*;
import com.github.jonteohr.tejbz.discord.listener.guild.GuildReady;
import com.github.jonteohr.tejbz.discord.listener.guild.NewMember;
import com.github.jonteohr.tejbz.discord.listener.guild.RoleRequest;
import com.github.jonteohr.tejbz.discord.listener.roles.SupporterRole;
import com.github.jonteohr.tejbz.discord.queue.ChannelEvent;
import com.github.jonteohr.tejbz.twitch.Twitch;
import com.github.jonteohr.tejbz.web.DashboardSocket;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class App {
	public static JDA jda;
	
	public static String prefix = "!";
	public static int color = 0x39A0FE;
	public static String authorImage = "https://static-cdn.jtvnw.net/jtv_user_pictures/7f35ded7-e1d7-4cb3-9a46-a47e8ff56e3a-profile_image-300x300.png";
	
	public static Guild guild;
	public static TextChannel general;
	public static TextChannel twitchLog;
	public static String logChannelId = "732559129268322375";
	
	public static boolean enableJoin = false;
	public static int joinLimit = 4;
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void main(String[] args) throws LoginException {

		// Read console input
		InputReader thread = new InputReader();
		thread.start();

		Collection<GatewayIntent> intents = new ArrayList<>(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));
		
		jda = JDABuilder.create(Credentials.TOKEN.getValue(), intents)
				.build();
		
		jda.addEventListener(new GuildReady());
		jda.addEventListener(new NewMember());
		jda.addEventListener(new RoleRequest());
		jda.addEventListener(new SupporterRole());
		jda.addEventListener(new ChannelEvent());
		jda.addEventListener(new BotMessage());
		
		// Commands
		jda.addEventListener(new Social());
		jda.addEventListener(new Follow());
		jda.addEventListener(new Stream());
		jda.addEventListener(new Video());
		jda.addEventListener(new Schedule());
		jda.addEventListener(new Help());
		jda.addEventListener(new Join());
		jda.addEventListener(new Github());
		jda.addEventListener(new Server());
		
		// Admin commands
		jda.addEventListener(new SetVideo());
		jda.addEventListener(new SetSchedule());
		jda.addEventListener(new ModHelp());
		jda.addEventListener(new Mute());
		jda.addEventListener(new StartJoin());
		
		// Misc
		jda.addEventListener(new VideoAnnouncer());
		
		VideoAnnouncer.videoTimer();
		
		DashboardSocket dashboardSocket = new DashboardSocket();
		try {
			dashboardSocket.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void onDisable() {
		System.out.println("***** SAVING DATA *****");

		System.out.println("Refreshing and saving credentials..");
		Identity.refreshToken(Twitch.getOAuth2());

		System.out.println("***** STOPPING BOT *****");
		System.exit(1);
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

	public static boolean isStringUppercase(String string) {
		StringBuffer stringBuffer = new StringBuffer();
		for(int k = 0; k < string.length(); k++) {
			if(Character.isSpaceChar(string.charAt(k)))
				stringBuffer.append(" ");
			else
				if(Character.isLetter(string.charAt(k)))
					stringBuffer.append(string.charAt(k));
		}

		char[] charArray = stringBuffer.toString().toCharArray();

		for(int i = 0; i < charArray.length; i++)
			if(!Character.isUpperCase(charArray[i]))
				return false;

		return true;
	}

}

class InputReader extends Thread {

	@Override
	public void run() {
		Scanner in = new Scanner(System.in);

		while(in.hasNext()) {
			String s = in.nextLine();

			if(s.equalsIgnoreCase("stop"))
				App.onDisable();
		}
	}
}