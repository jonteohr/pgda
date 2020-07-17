package com.jonteohr.discord.tejbz.twitch;

import java.util.Timer;
import java.util.TimerTask;

import com.jonteohr.discord.tejbz.PropertyHandler;

public class AutoMessage {
	public static int count = 0;
	
	public static void autoMessageTimer() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {	
				if(count < 10)
					return;
				
				PropertyHandler props = new PropertyHandler();
				Twitch.twitchClient.getChat().sendMessage("tejbz", "Check out Tejbz latest video: " + props.getPropertyValue("recent_video") + " tejbzSeemsgood");
				count = 0;
			}
		}, 2*60*1000, 2*60*1000);
	}
}
