package com.github.jonteohr.tejbz.twitch.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.github.jonteohr.tejbz.twitch.CommandCooldown;

public class CommandTimer {
	public static List<CommandCooldown> cooldown = new ArrayList<>();
	
	public static void startCommandCooldown() {
		cooldownTimer();
	}
	
	public static void addToCooldown(String command) {
		cooldown.add(new CommandCooldown(command, 7));
	}
	
	public static boolean isInCooldown(String command) {
		for (CommandCooldown commandCooldown : cooldown) {
			String curCommand = commandCooldown.getCommand();

			if (curCommand.equalsIgnoreCase(command))
				return true;
		}
		
		return false;
	}
	
	private static void cooldownTimer() {
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if(cooldown.size() < 1)
					return;
				
				for(int i = 0; i < cooldown.size(); i++) {
					String curCommand = cooldown.get(i).getCommand();
					int curCooldown = cooldown.get(i).getCooldown();
					
					if(curCooldown > 1) {
						curCooldown--;
						cooldown.set(i, new CommandCooldown(curCommand, curCooldown));
					} else {
						cooldown.remove(i);
					}
				}
			}
		}, 1000, 1000);
	}
}
