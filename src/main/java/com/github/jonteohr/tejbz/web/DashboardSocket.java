package com.github.jonteohr.tejbz.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.github.jonteohr.tejbz.twitch.Twitch;
import com.github.jonteohr.tejbz.twitch.automessage.AutoMessage;
import com.github.jonteohr.tejbz.twitch.sql.BlackList;
import com.github.jonteohr.tejbz.twitch.sql.CommandSQL;
import com.github.jonteohr.tejbz.twitch.sql.SettingsSQL;

public class DashboardSocket {
	
	public void start() throws IOException {
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
//			System.out.println("Received: " + clientSentence);
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			outToClient.writeBytes(capitalizedSentence);
			connectionSocket.close();
			
			onReceived(clientSentence);
		}
	}
	
	private void onReceived(String message) {
		String[] args = message.split("\\s+");
		
		CommandSQL sql = new CommandSQL();
		
		if(args[0].equalsIgnoreCase("cmd")) {
			Twitch.commands = sql.getCommandsMap();
			Twitch.specCommands = sql.getSpecialCommands();
			System.out.println("Updated local commands list");
		}
		
		if(args[0].equalsIgnoreCase("automessage")) {
			AutoMessage.updateAutoMessages();
			System.out.println("Updated local automessage list");
		}
		
		if(args[0].equalsIgnoreCase("settings")) {
			SettingsSQL settingsSql = new SettingsSQL();
			BlackList bList = new BlackList();
			
			Twitch.settings.put("preventLinks", (settingsSql.getSettingValue("preventLinks") == 1));
			Twitch.settings.put("allowMe", (settingsSql.getSettingValue("allowMe") == 1));
			Twitch.settings.put("excemptSubs", (settingsSql.getSettingValue("excemptSubs") == 1));
			BlackList.blockedPhrases = bList.getBlacklist();
			
			System.out.println("Updated local settings");
		}

		if(args[0].equalsIgnoreCase("commercial")) {
			if(args.length < 2)
				return;

			int time = Integer.parseInt(args[1]);

			Twitch.runAd(time);
		}

		if(args[0].equalsIgnoreCase("giveaway_winner")) {
			if(args.length < 2)
				return;

			Twitch.chatMe("THE WINNER OF THE GIVEAWAY IS @" + args[1] + "! Congratulations!");
//			Giveaway.resetList();
			return;
		}
	}
}
