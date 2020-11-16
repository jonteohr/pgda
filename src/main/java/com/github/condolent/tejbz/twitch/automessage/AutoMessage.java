package com.github.condolent.tejbz.twitch.automessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.condolent.tejbz.PropertyHandler;
import com.github.condolent.tejbz.twitch.Twitch;
import com.github.condolent.tejbz.twitch.sql.AutoMessageSQL;

public class AutoMessage {
	public static int count = 0;
	
	private static int pagination = 0;
	
	public static List<String> autoMessages = new ArrayList<>();
	
	public static void autoMessageTimer() {
		Timer timer = new Timer();
		AutoMessageSQL sql = new AutoMessageSQL();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {	
				if(count < sql.getInterval())
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
			}
		}, 7*60*1000, 7*60*1000);
	}

	public static void updateAutoMessages() {
		AutoMessageSQL sql = new AutoMessageSQL();
		List<String> newMessages = sql.getMessages();
		
		// Remove old ones
		for(int i = 0; i < autoMessages.size(); i++)
			if(!newMessages.contains(autoMessages.get(i)))
				autoMessages.remove(i);
		
		// Add new ones
		for (String newMessage : newMessages)
			if (!autoMessages.contains(newMessage))
				autoMessages.add(newMessage);
	}
}
