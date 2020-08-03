# PGDABot
Discord & Twitch chat bot for [Tejbz](https://twitch.tv/tejbz)

---

`com.jonteohr.discord.tejbz.Credentials.java`
```java
public enum Credentials {
	TOKEN("BOT_TOKEN"),
	OAUTH("OAUTH_TOKEN"),
	CHATOAUTH("CHATBOT_TOKEN"),
	BOTOAUTH("CHATBOT_TOKEN"),
	DB_HOST("localhost"),
	DB_NAME("dbname"),
	DB_USER("dbuser"),
	DB_PASS("passwd");
	
	private String val;

	Credentials(String string) {
		this.val = string;
	}
	
	public String getValue() {
		return val;
	}
	
}
```