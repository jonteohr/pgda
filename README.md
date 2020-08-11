# PGDABot
Discord & Twitch chat bot for [Tejbz](https://twitch.tv/tejbz)

---

## Develop & Contribute
1. Fork the repo.
2. Create your feature branch
    * `git checkout -b my-feature`
3. Create a Credentials class.
    1. Create `com.jonteohr.discord.tejbz.Credentials.java`
    2. Paste and change the values to match your setup:
    ```java
	public enum Credentials {
		TOKEN("BOT_TOKEN"),
		OAUTH("TEJBZ_OAUTH"),
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
4. Commit your changes
    * `git commit -m "Digested changelog"`
5. Push to your branch
    * `git push origin my-feature`
6. Create a new [Pull Request](https://github.com/condolent/pgda/compare)