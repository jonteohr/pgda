package com.github.jonteohr.tejbz.twitch;

import java.util.ArrayList;
import java.util.List;

import com.github.jonteohr.tejbz.twitch.sql.WatchTimeSQL;

public class BotList {
	public static List<String> botList = new ArrayList<>();
	
	public static void setBotList() {
		WatchTimeSQL sql = new WatchTimeSQL();
		
		botList = sql.getBotList();
	}
}
