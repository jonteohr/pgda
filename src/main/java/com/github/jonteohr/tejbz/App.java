/*
 * https://discord.com/api/oauth2/authorize?client_id=731137216264536124&permissions=500694208&scope=bot
 */

package com.github.jonteohr.tejbz;

import com.github.jonteohr.tejbz.discord.listener.SlashCommandListener;
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
import com.github.jonteohr.tejbz.twitch.Twitch;
import com.github.jonteohr.tejbz.web.DashboardSocket;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class App {
	public static JDA jda;
	
	public static final String prefix = "!";
	public static final int color = 0x39A0FE;
	public static final String authorImage = "https://static-cdn.jtvnw.net/jtv_user_pictures/7f35ded7-e1d7-4cb3-9a46-a47e8ff56e3a-profile_image-300x300.png";
	public static final boolean DEV_MODE = false;
	
	public static Guild guild;
	public static TextChannel general;
	public static TextChannel liveChannel;
	public static TextChannel twitchLog;
	public static final String logChannelId = "732559129268322375";
	
	public static boolean enableJoin = false;
	public static int joinLimit = 4;
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void main(String[] args) throws LoginException {

		// Read console input
		InputReader thread = new InputReader();
		thread.start();

		Collection<GatewayIntent> intents = new ArrayList<>(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));

		intents.add(GatewayIntent.GUILD_MESSAGES);
		
		jda = JDABuilder.create(Credentials.TOKEN.getValue(), intents)
				.build();
		
		jda.addEventListener(new GuildReady());
		jda.addEventListener(new NewMember());
		jda.addEventListener(new RoleRequest());
		jda.addEventListener(new SupporterRole());
		jda.addEventListener(new BotMessage());
		jda.addEventListener(new SlashCommandListener());
		
		// Commands
		jda.addEventListener(new Help());
		jda.addEventListener(new Join());
		
		// Admin commands
		jda.addEventListener(new SetVideo());
		jda.addEventListener(new SetSchedule());
		jda.addEventListener(new ModHelp());
		jda.addEventListener(new Mute());
		jda.addEventListener(new StartJoin());
		
		// Misc
		jda.addEventListener(new VideoAnnouncer());

		if(!DEV_MODE) {
			CommandListUpdateAction commandListUpdateAction = jda.updateCommands();

			commandListUpdateAction.addCommands(
					Commands.slash("social", "The links to Tejbz social channels."),
					Commands.slash("stream", "Current stream information."),
					Commands.slash("schedule", "This weeks streaming schedule."),
					Commands.slash("youtube", "The latest youtube video from Tejbz."),

					// Admin
					Commands.slash("modhelp", "A quick overview of the commands moderators can use."),
					Commands.slash("mute", "Mute a member from talking and chatting.")
							.addOption(OptionType.USER, "target", "The member you want to mute.", true),
					Commands.slash("setschedule", "Sets this weeks schedule image.")
							.addOption(OptionType.STRING, "image_url", "A url to the schedule image.", true),
					Commands.slash("setvideo", "Sets the recent video to be promoted by the bot.")
							.addOption(OptionType.STRING, "video_url", "The URL to the latest video", true)

			);

			commandListUpdateAction.queue();
		}
		
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

	public static boolean isStringUppercase(String string, int minLength) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int k = 0; k < string.length(); k++) {
			if(Character.isSpaceChar(string.charAt(k)))
				stringBuilder.append(" ");
			else
				if(Character.isLetter(string.charAt(k)))
					stringBuilder.append(string.charAt(k));
		}

		char[] charArray = stringBuilder.toString().toCharArray();

		if(charArray.length < minLength)
			return false;

		for (char c : charArray)
			if (!Character.isUpperCase(c))
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