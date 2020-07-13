package com.jonteohr.discord.tejbz.twitch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.helix.domain.FollowList;
import com.github.twitch4j.helix.domain.GameList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.github.twitch4j.helix.domain.SubscriptionList;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.jonteohr.discord.tejbz.credentials.Credentials;

public class Twitch {
	public static TwitchClient chatClient;
	public static TwitchClient twitchClient;

	public static OAuth2Credential OAuth2 = new OAuth2Credential("twitchify_bot", Credentials.OAUTH.getValue());
	public static OAuth2Credential chatOauth = new OAuth2Credential("rlhypr", Credentials.CHATOAUTH.getValue());
	public static OAuth2Credential chatBot = new OAuth2Credential("PGDABot", Credentials.BOTOAUTH.getValue());
	
	public static void initTwitch() {
		EventManager eventManager = new EventManager();
		eventManager.registerEventHandler(new SimpleEventHandler());
		
		twitchClient = TwitchClientBuilder.builder()
				.withEnableHelix(true)
				.withEventManager(eventManager)
				.withChatAccount(OAuth2)
				.withEnableChat(true)
				.withEnablePubSub(true)
				.withDefaultAuthToken(OAuth2)
				.build();
		
		chatClient = TwitchClientBuilder.builder()
				.withChatAccount(chatOauth)
				.withEnableChat(true)
				.withEventManager(eventManager)
				.build();
		
		chatClient.getChat().joinChannel("rlhypr");
		
		TwitchHandler twitchHandler = new TwitchHandler();
		eventManager.getEventHandler(SimpleEventHandler.class).registerListener(twitchHandler);
		
		twitchClient.getClientHelper().enableStreamEventListener("tejbz");
		
		twitchClient.getPubSub().listenForSubscriptionEvents(OAuth2, "25622462");
		twitchClient.getPubSub().listenForCheerEvents(OAuth2, "25622462");
		
		System.out.println("Twitch4J Finished loading and initiated.");
		System.out.println("Tejbz user ID: " + getUser("tejbz").getId());
	}
	
	/**
	 * Gets the game name from a GameID
	 * @param id a {@link java.lang.String String} gameID
	 * @return {@link java.lang.String String} game-name
	 */
	public static String getGameById(String id) {
		if(id.isEmpty() || id == "")
			return "No game set...";
		GameList resList = twitchClient.getHelix().getGames(OAuth2.getAccessToken(), Arrays.asList(id), null).execute();
		List<String> gamename = new ArrayList<String>();
		resList.getGames().forEach(game -> {
			gamename.add(game.getName());
		});
		
		return gamename.get(0);
	}
	
	/**
	 * 
	 * @param channel
	 * @return
	 */
	public static Stream getStream(String channel) {
		StreamList streamlist = twitchClient.getHelix().getStreams(OAuth2.getAccessToken(), null, null, 1, null, null, null, null, Arrays.asList(channel)).execute();

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
		FollowList reslist = twitchClient.getHelix().getFollowers(OAuth2.getAccessToken(), null, getUser(channel).getId(), null, 100).execute();
		
		return reslist.getTotal();
	}
	
	public static boolean isFollowing(String channel) {
		FollowList reslist = twitchClient.getHelix().getFollowers(OAuth2.getAccessToken(), getUser(channel).getId(), getUser("tejbz").getId(), null, 1).execute();
		
		if(reslist.getFollows().size() < 1)
			return false;
		
		return true;
	}
	
	public static int getSubscribers(String channel) {
		SubscriptionList reslist = twitchClient.getHelix().getSubscriptions(OAuth2.getAccessToken(), getUser(channel).getId(), null, null, 100).execute();
		
		int followers = reslist.getSubscriptions().size();
		int response = reslist.getSubscriptions().size();
		String pagination = reslist.getPagination().getCursor();
		
		do {
			reslist = twitchClient.getHelix().getSubscriptions(OAuth2.getAccessToken(), getUser(channel).getId(), pagination, null, 100).execute();
			followers += reslist.getSubscriptions().size();
			response = reslist.getSubscriptions().size();
			pagination = reslist.getPagination().getCursor();
		}
		while(response > 0);
		
		return followers;
	}
	
	public static User getUser(String channel) {
		UserList usr = twitchClient.getHelix().getUsers(OAuth2.getAccessToken(), null, Arrays.asList(channel)).execute();
		
		return usr.getUsers().get(0);
	}
}
