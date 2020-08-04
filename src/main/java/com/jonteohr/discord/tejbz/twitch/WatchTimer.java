package com.jonteohr.discord.tejbz.twitch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.github.twitch4j.tmi.domain.Chatters;
import com.jonteohr.discord.tejbz.sql.WatchTimeSQL;

public class WatchTimer {
	
	public static boolean streamLive = false;
	
	public static Map<String, Integer> watchList = new HashMap<String, Integer>();
	
	public static void countWatchTime() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(!streamLive) // Stream is offline, don't count!
					return;
				
				Chatters chatList = Twitch.twitchClient.getMessagingInterface().getChatters("tejbz").execute();
				List<String> viewers = chatList.getAllViewers();
				
				viewers.forEach(viewer -> {
					if(viewer.equalsIgnoreCase("tejbz")) // Don't want to record the broadcaster
						return;
					
					if(watchList.containsKey(viewer))
						watchList.replace(viewer, watchList.get(viewer) + 1);
					else
						watchList.put(viewer, 1);
				});
			}
		}, 1*60*1000, 1*60*1000);
	}
	
	public static void saveWatchTime() {
		Timer timer = new Timer();
		WatchTimeSQL sql = new WatchTimeSQL();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(!streamLive)
					return;
				
				Map<String, Integer> savedList = sql.getWatchTimeList();
				watchList.forEach((viewer, time) -> {
					if(savedList.containsKey(viewer)) {
						sql.incrementWatchTime(viewer, time);
					} else {
						sql.addToWatchTime(viewer, time);
					}
				});
			}
		}, 15*60*1000, 15*60*1000);
	}
}
