package com.github.jonteohr.tejbz.credentials;

public enum Credentials {
	TOKEN("TOKEN", ""),
	BOTOAUTH("BOT_OAUTH", ""),
	HYPROAUTH("HYPR_OAUTH", ""),
	DB_HOST("HOST", ""),
	DB_NAME("NAME", ""),
	DB_USER("USER", ""),
	DB_PASS("PASS", "");

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
