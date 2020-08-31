package com.jonteohr.tejbz.credentials;

public enum Credentials {
	TOKEN("DISCORD_TOKEN", ""),
	OAUTH("TEJBZ_OAUTH", "TEJBZ_OAUTH_REFRESH"),
	BOTOAUTH("PGDABOT_OAUTH", ""),
	DB_HOST("localhost", ""),
	DB_NAME("db_name", ""),
	DB_USER("db_user", ""),
	DB_PASS("db_passwd", "");
	
	private String accessToken;
	private String refreshToken;

	Credentials(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	public String getValue() {
		return accessToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
}
