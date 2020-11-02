package com.jonteohr.tejbz.twitch;

public class CommandCooldown {
	
	private final String name;
	private final int cooldown;
	
	public CommandCooldown(String name, int cooldown) {
		this.name = name;
		this.cooldown = cooldown;
	}
	
	public String getCommand() {
		return name;
	}
	
	public int getCooldown() {
		return cooldown;
	}

}
