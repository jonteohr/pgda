package com.jonteohr.tejbz.twitch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.github.twitch4j.tmi.domain.Chatters;
import com.jonteohr.tejbz.twitch.sql.WatchTimeSQL;

public class WatchTimer {
	
	public static Map<String, Integer> watchList = new HashMap<>();
	
	public static void countWatchTime() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(Twitch.getStream("tejbz") == null) // Stream is offline, don't count!
					return;
				
				Chatters chatList = Twitch.twitchClient.getMessagingInterface().getChatters("tejbz").execute();
				List<String> viewers = chatList.getAllViewers();
				
				viewers.forEach(viewer -> {
					if(BotList.botList.contains(viewer)) // Don't want to record the broadcaster, bots etc.
						return;
					
					if(watchList.containsKey(viewer)) {
						watchList.replace(viewer, watchList.get(viewer) + 1);
					} else
						watchList.put(viewer, 1);
				});
				
				WatchTimeSQL sql = new WatchTimeSQL();
				
				Map<String, Integer> savedList = sql.getWatchTimeList();
				watchList.forEach((viewer, time) -> {
					if(savedList.containsKey(viewer)) {
						if(!savedList.get(viewer).equals(watchList.get(viewer)))
							sql.setWatchTime(viewer, time);
					} else {
						sql.addToWatchTime(viewer, time);
					}
				});
			}
		}, 60 * 1000, 60 * 1000);
	}
}
