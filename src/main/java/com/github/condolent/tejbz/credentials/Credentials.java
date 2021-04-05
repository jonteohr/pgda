package com.github.condolent.tejbz.credentials;

public enum Credentials {
	TOKEN("DISCORD_TOKEN", ""),
	BOTOAUTH("PGDABOT_OAUTH", ""),
	HYPROAUTH("", ""),
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
