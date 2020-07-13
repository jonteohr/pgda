package com.jonteohr.discord.tejbz.credentials;

public enum Credentials {
	TOKEN("BOT_TOKEN"),
	OAUTH("HELIX OAUTH"),
	CHATOAUTH("MOD OAUTH"),
	BOTOAUTH("TWITCH BOT OAUTH");
	
	private String val;

	Credentials(String string) {
		this.val = string;
	}
	
	public String getValue() {
		return val;
	}
	
}
