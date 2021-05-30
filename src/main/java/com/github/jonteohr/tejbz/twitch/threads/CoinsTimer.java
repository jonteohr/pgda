package com.github.jonteohr.tejbz.twitch.threads;

import java.util.*;

public class CoinsTimer {

	private static final int cooldown = 15;
	private static Map<String, Integer> cooldownList = new TreeMap<>();

	public static void start() {
		coinsTimer();
	}

	public static boolean isCooldown(String user) {
		return cooldownList.containsKey(user);
	}

	public static void activateCooldown(String user) {
		if(cooldownList.containsKey(user))
			cooldownList.replace(user, cooldown);
		else
			cooldownList.put(user, cooldown);
	}

	public static int getCooldown(String user) {
		if(cooldownList.containsKey(user))
			return cooldownList.get(user);
		else
			return -1;
	}

	private static void coinsTimer() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(cooldownList.size() < 1)
					return;

				List<String> removes = new ArrayList<>();

				for(Map.Entry<String, Integer> entry : cooldownList.entrySet()) {
					if(entry.getValue() > 1)
						entry.setValue(entry.getValue() - 1);
					else
						removes.add(entry.getKey());
				}

				removes.forEach(key ->  cooldownList.remove(key));
			}
		}, 60*1000, 60*1000);
	}
}
