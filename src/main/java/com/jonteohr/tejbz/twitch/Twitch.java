package com.jonteohr.tejbz.twitch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.ChannelInformation;
import com.github.twitch4j.helix.domain.FollowList;
import com.github.twitch4j.helix.domain.Game;
import com.github.twitch4j.helix.domain.GameList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.SubscriptionList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.jonteohr.tejbz.PropertyHandler;
import com.jonteohr.tejbz.credentials.Credentials;
import com.jonteohr.tejbz.credentials.Identity;
import com.jonteohr.tejbz.credentials.RefreshToken;
import com.jonteohr.tejbz.twitch.automessage.AutoMessage;
import com.jonteohr.tejbz.twitch.sql.AutoMessageSQL;
import com.jonteohr.tejbz.twitch.sql.BlackList;
import com.jonteohr.tejbz.twitch.sql.CommandSQL;
import com.jonteohr.tejbz.twitch.sql.SettingsSQL;
import com.jonteohr.tejbz.twitch.sql.WatchTimeSQL;

public class Twitch {
	public static TwitchClient twitchClient;

	public static OAuth2Credential OAuth2;
	public static OAuth2Credential chatBot = new OAuth2Credential("twitch", Credentials.BOTOAUTH.getValue());
	
	public static Map<String, String> commands = new HashMap<String, String>();
	public static Map<String, String> specCommands = new HashMap<String, String>();
	public static Map<String, Boolean> settings = new HashMap<String, Boolean>();
	
	public static void initTwitch() {
		EventManager eventManager = new EventManager();
		eventManager.registerEventHandler(new SimpleEventHandler());
		
		// Build the twitch instance
		twitchClient = TwitchClientBuilder.builder()
				.withEnableHelix(true)
				.withEnableKraken(true)
				.withEnableChat(true)
				.withEnablePubSub(false)
				.withEnableTMI(true)
				.withEventManager(eventManager)
				.withDefaultAuthToken(OAuth2)
				.withChatAccount(chatBot)
				.withScheduledThreadPoolExecutor(new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors()))
				.build();
		
		// Refresh the OAuth2 token!
		Identity identity = new Identity();
		PropertyHandler props = new PropertyHandler();
		OAuth2 = new OAuth2Credential("twitch", props.getPropertyValue("access_token"), props.getPropertyValue("refresh_token"), null, null, null, null);
		OAuth2 = identity.refreshToken(OAuth2);
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new RefreshToken(), 60*60*1000, 60*60*1000); // Make sure we keep updating the token
		
		// Do Twitch stuff
		twitchClient.getChat().joinChannel("tejbz");
		
		TwitchHandler twitchHandler = new TwitchHandler();
		eventManager.getEventHandler(SimpleEventHandler.class).registerListener(twitchHandler);
		
		twitchClient.getClientHelper().enableStreamEventListener("25622462", "tejbz");
		
		System.out.println("Twitch4J Finished loading and initiated.");
		System.out.println("Tejbz user ID: " + getUser("tejbz").getId());
		
		CommandSQL sql = new CommandSQL();
		AutoMessageSQL amSQL = new AutoMessageSQL();
		
		commands = sql.getCommandsMap();
		specCommands = sql.getSpecialCommands();
		AutoMessage.autoMessages = amSQL.getMessages();
		sqlUpdater();
		AutoMessage.autoMessageTimer();
		
		// Watch time timers
		WatchTimer.countWatchTime();
		WatchTimeSQL watchTimeSQL = new WatchTimeSQL();
		WatchTimer.watchList = watchTimeSQL.getWatchTimeList();
		BotList.setBotList();
		
		// Fetch settings
		getSettings();
		BlackList blackList = new BlackList();
		BlackList.blockedPhrases = blackList.getBlacklist();
	}
	
	/**
	 * Gets the game name from a GameID
	 * @param id a {@link java.lang.String String} gameID
	 * @return {@link java.lang.String String} game-name
	 */
	public static String getGameById(String id) {
		if(id == null || id == "")
			return "No game set...";
		
		GameList resList = twitchClient.getHelix().getGames(Identity.getAccessToken(OAuth2), Arrays.asList(id), null).execute();
		
		return resList.getGames().get(0).getName();
	}
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public static Stream getStream(String channel) {
		StreamList streamlist = twitchClient.getHelix().getStreams(Identity.getAccessToken(OAuth2), null, null, 1, null, null, null, null, Arrays.asList(channel)).execute();

		if(streamlist.getStreams().size() < 1)
			return null;
		
		return streamlist.getStreams().get(0);
	}
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public static int getFollowers(String channel) {
		FollowList reslist = twitchClient.getHelix().getFollowers(Identity.getAccessToken(OAuth2), null, getUser(channel).getId(), null, null).execute();
		
		return reslist.getTotal();
	}
	
	public static boolean isFollowing(String channel) {
		FollowList reslist = twitchClient.getHelix().getFollowers(Identity.getAccessToken(OAuth2), getUser(channel).getId(), getUser("tejbz").getId(), null, 1).execute();
		
		if(reslist.getFollows().size() < 1)
			return false;
		
		return true;
	}
	
	public static boolean isSubscribed(String user) {
		SubscriptionList subList  = twitchClient.getHelix().getSubscriptionsByUser(Identity.getAccessToken(OAuth2), getUser("tejbz").getId(), Arrays.asList(getUser(user).getId())).execute();
		if(subList.getSubscriptions().size() > 0)
			return true;
		
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public static void setTitle(String title) {
		twitchClient.getKraken().updateTitle(Identity.getAccessToken(OAuth2), getUser("tejbz").getId(), title).execute();
	}
	
	public static void setGame(String game) {
		GameList res = twitchClient.getHelix().getGames(Identity.getAccessToken(OAuth2), null, Arrays.asList(game)).execute();
		Game fetchedGame = res.getGames().get(0);
		
		ChannelInformation channelInfo = new ChannelInformation()
				.withGameId(fetchedGame.getId());
		
		twitchClient.getHelix().updateChannelInformation(Identity.getAccessToken(OAuth2), getUser("tejbz").getId(), channelInfo).execute();	
	}
	
	public static ChannelInformation getChannelInfo() {
		ChannelInformation channelInfo = twitchClient.getHelix().getChannelInformation(Identity.getAccessToken(OAuth2), Arrays.asList(getUser("tejbz").getId())).execute().getChannels().get(0);
		
		return channelInfo;
	}
	
	public static int getSubscribers(String channel) {
		SubscriptionList reslist = twitchClient.getHelix().getSubscriptions(Identity.getAccessToken(OAuth2), getUser(channel).getId(), null, null, 100).execute();
		int subs = reslist.getSubscriptions().size();
		int response = reslist.getSubscriptions().size();
		String pagination = reslist.getPagination().getCursor();
		
		do {
			reslist = twitchClient.getHelix().getSubscriptions(Identity.getAccessToken(OAuth2), getUser(channel).getId(), pagination, null, 100).execute();
			subs += reslist.getSubscriptions().size();
			response = reslist.getSubscriptions().size();
			pagination = reslist.getPagination().getCursor();
		}
		while(response > 0);
		
		return subs;
	}
	
	public static User getUser(String channel) {
		UserList usr = twitchClient.getHelix().getUsers(chatBot.getAccessToken(), null, Arrays.asList(channel)).execute();
		
		return usr.getUsers().get(0);
	}
	
	public static String getWatchTime(String user) {
		int total = (WatchTimer.watchList.containsKey(user.toLowerCase()) ? WatchTimer.watchList.get(user.toLowerCase()) : 0);
		int d = total / 24 / 60;
		int h = total / 60 % 24;
		int m = total % 60;
		
		if(d > 0)
			return d + "days, " + h + " hours and " + m + " minutes";
		else
			return h + " hours and " + m + " minutes";
	}
	
	public static String getFollowAge(String user) {
		FollowList reslit = Twitch.twitchClient.getHelix().getFollowers(Identity.getAccessToken(OAuth2), Twitch.getUser(user).getId(), Twitch.getUser("tejbz").getId(), null, 1).execute();
		
		LocalDateTime followDate = reslit.getFollows().get(0).getFollowedAt();
		LocalDateTime currentDate = LocalDateTime.now();
		LocalDateTime tempDate = LocalDateTime.from(followDate);
		
		long years = tempDate.until(currentDate, ChronoUnit.YEARS);
		tempDate = tempDate.plusYears(years);
		
		long months = tempDate.until(currentDate, ChronoUnit.MONTHS);
		tempDate = tempDate.plusMonths(months);
		
		long days = tempDate.until(currentDate, ChronoUnit.DAYS);
		
		if(years > 0)
			return years + " years, " + months + " months and " + days + " days";
		else if(months > 0)
			return months + " months and " + days + " days";
		else
			return days + " days";
	}
	
	private static void sqlUpdater() {
		Timer timer = new Timer();
		CommandSQL sql = new CommandSQL();
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				commands = sql.getCommandsMap();
				specCommands = sql.getSpecialCommands();
				
				AutoMessage.updateAutoMessages();
				
				getSettings();
			}
		}, 15*1000, 15*1000);
	}
	
	private static void getSettings() {
		SettingsSQL sql = new SettingsSQL();
		BlackList bList = new BlackList();
		
		settings.put("preventLinks", (sql.getSettingValue("preventLinks") == 1 ? true : false));
		settings.put("allowMe", (sql.getSettingValue("allowMe") == 1 ? true : false));
		settings.put("excemptSubs", (sql.getSettingValue("excemptSubs") == 1 ? true : false));
		BlackList.blockedPhrases = bList.getBlacklist();
	}
}