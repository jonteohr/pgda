package com.github.jonteohr.tejbz.mcping;

import java.io.IOException;

public class MinecraftStats {

	private int currentPlayers;
	private int maxPlayers;
	private int protocol;
	private String motd;
	private String favicon;
	private String version;

	/**
	 * Calls a query towards the minecraft server
	 * @throws IOException if server is offline
	 */
	public MinecraftStats() throws IOException {
		MinecraftPingReply data = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname("mc.pgda.xyz").setPort(25565));

		currentPlayers = data.getPlayers().getOnline();
		maxPlayers = data.getPlayers().getMax();
		motd = data.getDescription().getText();
		favicon = data.getFavicon();
		version = data.getVersion().getName();
		protocol = data.getVersion().getProtocol();
	}

	/**
	 * The amount of players currently online on the server
	 * @return a {@link Integer} number of current players
	 */
	public int getCurrentPlayers() {
		return currentPlayers;
	}

	/**
	 * The current protocol the server is running
	 * @return a {@link Integer} protocol number
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * The max amount of players the server allows
	 * @return a {@link Integer} number
	 */
	public int getMaxPlayers() {
		return maxPlayers;
	}

	/**
	 * The set message of the day of the server
	 * @return a {@link String} of the set motd
	 */
	public String getMotd() {
		return motd;
	}

	/**
	 * The set favicon name of the server
	 * @return a {@link String} favicon name
	 */
	public String getFavicon() {
		return favicon;
	}

	/**
	 * The complete version name of the server
	 * @return a {@link String} with complete version information
	 */
	public String getVersion() {
		return version;
	}
}
