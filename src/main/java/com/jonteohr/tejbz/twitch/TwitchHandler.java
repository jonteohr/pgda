package com.jonteohr.tejbz.twitch;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.helix.domain.Stream;
import com.jonteohr.tejbz.App;
import com.jonteohr.tejbz.credentials.Identity;
import com.jonteohr.tejbz.twitch.automessage.AutoMessage;
import com.jonteohr.tejbz.twitch.sql.AutoMessageSQL;
import com.jonteohr.tejbz.twitch.sql.BlackList;
import com.jonteohr.tejbz.twitch.sql.CommandSQL;
import com.jonteohr.tejbz.web.WebLog;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class TwitchHandler {
	
	private int clipTime = 0;
	private String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
	Pattern pattern = Pattern.compile(URL_REGEX);
	
	@EventSubscriber
	public void onTwitchChat(IRCMessageEvent e) {
		if(!e.getChannel().getName().equalsIgnoreCase("tejbz"))
			return;
		
		if(!e.getMessage().isPresent() || e.getUser() == null)
			return;
		
		// Increment to counter for automessaging
		AutoMessage.count++;
		
		String[] args = e.getMessage().get().split("\\s+");
		CommandSQL sql = new CommandSQL();
		
		String user = e.getTags().get("display-name");
		
		if((Twitch.settings.get("excemptSubs") == true && !Twitch.isSubscribed(user)) || (Twitch.settings.get("excemptSubs") == false)) {
			// Link check
			if((Twitch.settings.get("preventLinks") == true) && !isModerator(e.getTags())) {
				for(int i = 0; i < args.length; i++) {
					Matcher m = pattern.matcher(args[i]);
					if(m.find()) {
						if(args[i].contains("clips.twitch.tv"))
							continue;
						
						chat("/timeout " + user + " 3 Don't post links.");
						chat(user + " Links are not allowed! tejbzW (1s)");
						
						System.out.println("Removed " + user + "s message due to links disabled.");
						break;
					}
				}
			}
			
			// Blacklisted words check
			if(!isModerator(e.getTags())) {
				for(int i = 0; i < args.length; i++) {
					if(BlackList.blockedPhrases.contains(args[i])) {
						chat("/timeout " + user + " 3 Used a blacklisted word/phrase");
						chat(user + " You're using a blacklisted word/phrase! (1s)");
						
						System.out.println("Removed " + user + "s message due to blacklisted word.");
						return;
					}
				}
			}
			
			// Used a /me prefix
			if(!Twitch.settings.get("allowMe") && !isModerator(e.getTags())) {
				if(args[0].equalsIgnoreCase("ACTION")) {
					chat("/timeout " + user + " 3 Not allowed to use /me");
					chat(user + " you're not allowed to use /me (1s)");
					return;
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("!clip")) {
			if(Twitch.getStream("tejbz") == null) {
				chat("@" + user + " Tejbz is offline, there's nothing to clip!");
				return;
			}
			
			if(clipTime > 0) // Clip cooldown
				return;
			
			CreateClipList clipData = Twitch.twitchClient.getHelix().createClip(Identity.getAccessToken(Twitch.OAuth2), "25622462", false).execute();
			String clipLink = "https://clips.twitch.tv/" + clipData.getData().get(0).getId();
			chat("@" + user + " " + clipLink);

			EmbedBuilder msg = new EmbedBuilder();
			msg.setAuthor(user, "https://twitch.tv/" + user, Twitch.getUser(user).getProfileImageUrl());
			msg.setDescription("Just clipped Tejbz stream. Check it out!\n" + clipLink);
			
			App.twitchLog.sendMessage(msg.build()).queue();
			
			clipTimer();
			return;
		}
		
		if(args[0].equalsIgnoreCase("!commands")) {
			if(args.length == 1) {
				chat("@" + user + " List of commands are available at: http://pgda.xyz/commands");
				return;
			}
		}
		
		if(args[0].equalsIgnoreCase("!vanish")) {
			if(isModerator(e.getTags()))
				return;
			
			chat("/timeout " + user + " 1");
			chat(user + " Disappeared into the mist...");
			return;
		}
		
		if(isModerator(e.getTags())) {
			if(args[0].equalsIgnoreCase("!commands")) {
				if(args.length >= 3) {
					
					String setting = args[1];
					
					if(setting.equalsIgnoreCase("add")) {
						if(args.length < 4) {
							chat(user + " No reply specified.");
							return;
						}
						
						String cmdName = args[2];
						String msg = "";
						for(int i = 3; i < args.length; i++) {
							msg = msg + " " + args[i];
						}
						
						if(sql.getCommands().contains(cmdName)) {
							chat("@" + user + " The command " + cmdName + " already exists. Did you mean to use !editcmd maybe?");
							return;
						}
						
						if(sql.addCommand(cmdName, msg)) {
							chat("@" + user + " Command " + cmdName + " stored!");
							Twitch.commands.put(cmdName, msg);
							WebLog.addToWeblog("TWITCH", user, "Created the command <code>" + cmdName + "</code>");
						}
						
						return;
					}
					
					if(setting.equalsIgnoreCase("edit")) {
						if(args.length < 4) {
							chat(user + " No new reply specified.");
							return;
						}
						
						String cmdName = args[2];
						String msg = "";
						for(int i = 3; i < args.length; i++) {
							msg = msg + " " + args[i];
						}
						
						if(!sql.getCommands().contains(cmdName)) {
							chat("@" + user + " There is no command named " + cmdName);
							return;
						}
						
						if(sql.editCommand(cmdName, msg)) {
							chat("@" + user + " Command " + cmdName + " stored!");
							Twitch.commands.replace(cmdName, msg);
							WebLog.addToWeblog("TWITCH", user, "Edited the command <code>" + cmdName + "</code>");
						} else {
							chat("@" + user + " Failed editing the command " + cmdName);
						}
						
						return;
					}
					
					if(setting.equalsIgnoreCase("delete")) {
						if(!sql.getCommands().contains(args[2])) {
							chat("@" + user + " There is no command named " + args[2]);
							return;
						}
						
						if(sql.deleteCommand(args[2])) {
							chat("@" + user + " Command " + args[2] + " successfully deleted!");
							Twitch.commands.remove(args[2]);
							WebLog.addToWeblog("TWITCH", user, "Deleted the command <code>" + args[2] + "</code>");
						}
						
						return;
					}
				}
			}
			if(args[0].equalsIgnoreCase("!automessage")) {
				if(args.length < 3) {
					chat("@" + user + " Invalid arguments. Visit http://pgda.xyz/commands for commands list.");
					return;
				}
				
				String setting = args[1];
				
				if(setting.equalsIgnoreCase("interval")) {
					AutoMessageSQL amSql = new AutoMessageSQL();
					
					if(amSql.setInterval(Integer.parseInt(args[2]))) {
						chat("@" + user + " Successfully saved the auto-message delay to: " + args[2] + "!");
						WebLog.addToWeblog("TWITCH", user, "Changed the automessage interval to " + args[2]);
						return;
					}
				} else if(setting.equalsIgnoreCase("add")) {
					String message = "";
					for(int i = 2; i < args.length; i++) {
						message += args[i] + " ";
					}
					
					AutoMessageSQL amSql = new AutoMessageSQL();
					
					if(!amSql.addAutoMessage(message)) {
						chat("Failed to add/update playlist. Try again later!");
						return;
					}
					
					AutoMessage.updateAutoMessages();
					
					chat("Added message and updated playlist.");
					
					WebLog.addToWeblog("TWITCH", user, "Added a message to auto-message: <code>" + message + "</code>");
					return;
				} else if(setting.equalsIgnoreCase("remove")) {
					String message = "";
					for(int i = 2; i < args.length; i++) {
						message += args[i] + " ";
					}
					
					AutoMessageSQL amSql = new AutoMessageSQL();
					
					if(!amSql.removeAutoMessage(message)) {
						chat("Failed to remove/update playlist. Try again later!");
						return;
					}
					
					AutoMessage.updateAutoMessages();
					
					chat("Removed the message from the playlist!");
					WebLog.addToWeblog("TWITCH", user, "Removed a message from auto-message: <code>" + message + "</code>");
					return;
				}
				
			}
			
			if(args[0].equalsIgnoreCase("!title")) {
				if(args.length < 2) {
					String title = Twitch.getChannelInfo().getTitle();
					chat("Current title set to: " + title);
					return;
				}
				
				String title = "";
				for(int i = 1; i < args.length; i++) {
					title += args[i] + " ";
				}
				
				Twitch.setTitle(title);
				
				chat("Title set to: " + title);
				
				return;
			}
			
			if(args[0].equalsIgnoreCase("!game")) {
				if(args.length < 2) {
					String game = Twitch.getChannelInfo().getGameName();
					chat("Tejbz is currently playing " + game);
					return;
				}
				
				String game = "";
				for(int i = 1; i < args.length; i++) {
					if(i == args.length-1)
						game += args[i];
					else
						game += args[i] + " ";
				}
				
				Twitch.setGame(game);
				
				chat("Game set to: " + game);
				
				return;
			}
			
			if(args[0].equalsIgnoreCase("!help")) {
				chat("@" + user + " Bot formatting and commands are available over at http://pgda.xyz/commands");
				return;
			}
		}
		
		if(Twitch.commands.containsKey(args[0].toLowerCase())) {
			String reply = Twitch.commands.get(args[0]);
			
			if(Twitch.specCommands.containsKey(args[0])) {
				if(Twitch.specCommands.get(args[0]).equalsIgnoreCase("mod") && !isModerator(e.getTags()))
					return;
				if(Twitch.specCommands.get(args[0]).equalsIgnoreCase("sub") && !Twitch.isSubscribed(user))
					return;
			}
			
			if(reply.contains("[@user]"))
				reply = reply.replace("[@user]", "@" + user);
			if(reply.contains("[user]"))
				reply = reply.replace("[user]", user);
			if(reply.contains("[subcount]"))
				reply = reply.replace("[subcount]", Twitch.getSubscribers("tejbz") + "");
			if(reply.contains("[follows]"))
				reply = reply.replace("[follows]", "" + Twitch.getFollowers("tejbz"));
			if(reply.contains("[count]"))
				reply = reply.replace("[count]", String.valueOf(CommandSQL.getUses(args[0])));
			if(reply.contains("[watchtime]"))
				reply = reply.replace("[watchtime]", Twitch.getWatchTime(user));
			if(reply.contains("[followage]"))
				reply = reply.replace("[followage]", Twitch.getFollowAge(user));
			if(reply.contains("[uptime]")) {
				Stream stream = Twitch.getStream("tejbz");
				if(stream == null) {
					reply = "Tejbz is offline.";
				} else {
					reply = reply.replace("[uptime]", App.formatDuration(stream.getUptime()));
				}
			}
			if(reply.contains("[touser]"))
				reply = reply.replace("[touser]", (args[1] != null ? args[1] : user));
			if(reply.contains("[@touser]"))
				reply = reply.replace("[@touser]", (args[1] != null ? "@" + args[1] : "@" + user));
				
			
			chat(reply);
			CommandSQL.incrementUses(args[0]);
			return;
		}
	}
	
	@EventSubscriber
	public void onLive(ChannelGoLiveEvent e) {
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("Tejbz", null, Twitch.getUser(e.getChannel().getName()).getProfileImageUrl());
		msg.setColor(App.color);
		msg.setImage(e.getStream().getThumbnailUrl(1280, 720));
		msg.setTitle("Tejbz just went live!", "https://www.twitch.tv/tejbz");
		msg.addField("Title", e.getStream().getTitle(), false);
		msg.addField("Playing", Twitch.getGameById(e.getStream().getGameId()), false);
		
		App.general.sendMessage(App.guild.getPublicRole().getAsMention()).queue();
		App.general.sendMessage(msg.build()).queue();
	}
	
	@EventSubscriber
	public void onChatSub(SubscriptionEvent e) {
		String user = e.getUser().getName();
		int months = e.getMonths();
		int streak = e.getSubStreak();
		String message = (e.getMessage().isPresent() ? e.getMessage().get() : "No message..");
		String tier = (e.getSubscriptionPlan().contains("Prime") ? "Twitch Prime" : "A Tier " + e.getSubscriptionPlan().replace("0", "") + " sub");
		
		if(e.getGifted())
			return;
		
		/*
		 * Twitch Chat
		 */
		if(Twitch.twitchClient.getChat().isChannelJoined("tejbz"))
			if(months <= 1)
				chatMe("tejbzWave Welcome to the squad, @" + user + " tejbzLove");
			else
				chatMe("tejbzWave Welcome back @" + user + " tejbzLove");
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(App.color);
		
		msg.setAuthor(user, "https://twitch.tv/" + user, Twitch.getUser(user).getProfileImageUrl());
		msg.setDescription("Just subscribed with " + tier + ". They've been subscribed for " + months + " months.");
		if(!message.equalsIgnoreCase("No message.."))
			msg.addField("Message", message, true);
		if(streak > 0)
			msg.addField("Streak", streak + " months", true);
		
		App.twitchLog.sendMessage(msg.build()).queue();
	}
	
	@EventSubscriber
	public void onGiftedSub(GiftSubscriptionsEvent e) {
		String user = e.getUser().getName();
		int count = e.getCount();
		int totalGifted = e.getTotalCount();
		
		// Twitch Chat
		if(Twitch.twitchClient.getChat().isChannelJoined("tejbz"))
			chatMe("tejbzPog Thanks for the gifted, @" + user + " tejbzLove");
		
		TextChannel channel = App.twitchLog;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(App.color);
		msg.setAuthor(user, "https://twitch.tv/" + user, Twitch.getUser(user).getProfileImageUrl());
		msg.setDescription("Just gifted " + count + " subs to the community! They've gifted a total of " + totalGifted + " subs.");
		
		channel.sendMessage(msg.build()).queue();
	}
	
	/**
	 * Sends a message to Tejbz chat
	 * @param msg
	 */
	private void chat(String msg) {
		Twitch.twitchClient.getChat().sendMessage("tejbz", msg);
	}
	
	/**
	 * Sends a colored message to Tejbz chat
	 * @param msg
	 */
	private void chatMe(String msg) {
		Twitch.twitchClient.getChat().sendMessage("tejbz", "/me " + msg);
	}
	
	private void clipTimer() {
		clipTime = 30;
		
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(clipTime <= 0)
					return;
				
				clipTime--;
			}
		}, 30*1000);
	}
	
	/**
	 * Checks to see if the user is a moderator or above
	 * @param tags a {@link java.util.Map Map} with the users' tags
	 * @return {@code true} if yes
	 */
	private boolean isModerator(Map<String, String> tags) {
		if(tags.containsKey("badges") && tags.get("badges") != null) {
			if(tags.get("badges").contains("broadcaster") || tags.get("badges").contains("moderator")) {
				return true;
			}
		}
		
		return false;
	}
	
}