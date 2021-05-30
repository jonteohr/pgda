package com.github.jonteohr.tejbz.discord.listener;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.discord.listener.commands.Video;

import com.github.jonteohr.tejbz.mcping.MinecraftStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VideoAnnouncer extends ListenerAdapter {
	private static int count = 0;
	private static final int fire = 30;
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		if(!e.getGuild().equals(App.guild))
			return;
		
		if(!e.getChannel().equals(App.general))
			return;
		
		if(e.getAuthor().isBot())
			return;
		
		if(count < fire) {
			count++;
			return;
		}
	}
	
	public static void videoTimer() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(count >= fire) {
					Random r = new Random();
					if(r.nextInt(1) == 0)
						Video.sendAd(App.general);
					else {
						EmbedBuilder embedBuilder = new EmbedBuilder();
						embedBuilder.setAuthor("PGDA Minecraft Server", null, "https://pgda.xyz/server-icon.png");
						embedBuilder.setColor(App.color);
						try {
							MinecraftStats data = new MinecraftStats();

							embedBuilder.setDescription("Current information on the minecraft server.");

							embedBuilder.addField("Players", data.getCurrentPlayers() + "/" + data.getMaxPlayers(), true);
							embedBuilder.addField("Version", data.getVersion() + "", true);
							embedBuilder.addField("Adress", "mc.pgda.xyz", false);
						} catch (IOException ioException) {
							embedBuilder.setDescription("Server is currently offline.");
						}

						App.general.sendMessage(embedBuilder.build()).queue();
					}

					count = 0;
					return;
				}
			}
		}, 7*60*1000, 7*60*1000);
	}
}
