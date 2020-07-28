package com.jonteohr.discord.tejbz.twitch.automessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jonteohr.discord.tejbz.PropertyHandler;
import com.jonteohr.discord.tejbz.sql.AutoMessageSQL;
import com.jonteohr.discord.tejbz.twitch.Twitch;

public class AutoMessage {
	public static int count = 0;
	
	private static int pagination = 0;
	
	public static List<String> autoMessages = new ArrayList<String>();
	
	public static void autoMessageTimer() {
		Timer timer = new Timer();
		PropertyHandler props = new PropertyHandler();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {	
				if(count < Integer.parseInt(props.getPropertyValue("automessage_delay")))
					return;
				
				String message = autoMessages.get(pagination);
				
				if(message.contains("[video]")) {
					PropertyHandler props = new PropertyHandler();
					message = message.replace("[video]", props.getPropertyValue("recent_video"));
				}
				
				Twitch.twitchClient.getChat().sendMessage("tejbz", message);
				
				count = 0;
				if(pagination == (autoMessages.size() - 1))
					pagination = 0;
				else
					pagination++;
				
				System.out.println("Pagination is now: " + pagination);
				System.out.println("Next message: " + autoMessages.get(pagination));
			}
		}, 15*60*1000, 15*60*1000);
	}

	public static void updateAutoMessages() {
		AutoMessageSQL sql = new AutoMessageSQL();
		List<String> newMessages = sql.getMessages();
		
		// Remove old ones
		for(int i = 0; i < autoMessages.size(); i++)
			if(!newMessages.contains(autoMessages.get(i)))
				autoMessages.remove(i);
		
		// Add new ones
		for(int i = 0; i < newMessages.size(); i++)
			if(!autoMessages.contains(newMessages.get(i)))
				autoMessages.add(newMessages.get(i));
	}
}
