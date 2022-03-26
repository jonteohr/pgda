package com.github.jonteohr.tejbz.discord.listener.commands;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.mcping.MinecraftStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;

public class Server extends ListenerAdapter {
	public void onMessageReceived(MessageReceivedEvent e) {
		String[] args = e.getMessage().getContentRaw().split("\\s+");

		if(!args[0].equalsIgnoreCase(App.prefix + "server"))
			return;

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

		e.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
	}
}
