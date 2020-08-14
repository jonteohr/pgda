package com.jonteohr.discord.tejbz.credentials;

public enum Credentials {
	TOKEN("DISCORD_TOKEN"),
	OAUTH("TEJBZ_OAUTH"),
	BOTOAUTH("PGDABOT_OAUTH"),
	DB_HOST("localhost"),
	DB_NAME("db_name"),
	DB_USER("db_user"),
	DB_PASS("db_passwd");
	
	private String val;

	Credentials(String string) {
		this.val = string;
	}
	
	public String getValue() {
		return val;
	}
	
}
