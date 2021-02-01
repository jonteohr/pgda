package com.github.condolent.tejbz.discord.listener.commands;

import com.github.condolent.tejbz.App;
import com.github.condolent.tejbz.mcping.MinecraftPing;
import com.github.condolent.tejbz.mcping.MinecraftPingOptions;
import com.github.condolent.tejbz.mcping.MinecraftPingReply;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class Server extends ListenerAdapter {
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");

		if(!args[0].equalsIgnoreCase(App.prefix + "server"))
			return;

		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setAuthor("PGDA Minecraft Server", null, "https://pgda.xyz/server-icon.png");
		embedBuilder.setColor(App.color);

		try {
			MinecraftPingReply data = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname("mc.pgda.xyz").setPort(25565));

			embedBuilder.setDescription("Current information on the minecraft server.");

			embedBuilder.addField("Players", data.getPlayers().getOnline() + "/" + data.getPlayers().getMax(), true);
			embedBuilder.addField("Version", data.getVersion().getName() + "", true);
			embedBuilder.addField("Adress", "mc.pgda.xyz", false);
		} catch (IOException ioException) {
			embedBuilder.setDescription("Server is currently offline.");
		}

		e.getChannel().sendMessage(embedBuilder.build()).queue();
	}
}
