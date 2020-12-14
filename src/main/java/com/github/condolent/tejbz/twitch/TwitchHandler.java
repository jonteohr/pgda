package com.github.condolent.tejbz.twitch;

import com.github.condolent.tejbz.App;
import com.github.condolent.tejbz.PropertyHandler;
import com.github.condolent.tejbz.credentials.Identity;
import com.github.condolent.tejbz.twitch.automessage.AutoMessage;
import com.github.condolent.tejbz.twitch.sql.AutoMessageSQL;
import com.github.condolent.tejbz.twitch.sql.BlackList;
import com.github.condolent.tejbz.twitch.sql.CommandSQL;
import com.github.condolent.tejbz.twitch.sql.Giveaway;
import com.github.condolent.tejbz.twitch.threads.CommandTimer;
import com.github.condolent.tejbz.web.WebLog;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitchHandler {
	
	private int clipTime = 0;
	private final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
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
		
		if((Twitch.settings.get("excemptSubs") && !Twitch.isSubscribed(user)) || (!Twitch.settings.get("excemptSubs"))) {
			// Link check
			if((Twitch.settings.get("preventLinks")) && !Twitch.isModerator(e.getTags())) {
				for(String arg : args) {
					Matcher m = pattern.matcher(arg);
					if(m.find()) {
						if(arg.contains("clips.twitch.tv"))
							continue;

						Twitch.chat("/timeout " + user + " 3 Don't post links.");
						Twitch.chat(user + " Links are not allowed! tejbzW (1s)");

						System.out.println("Removed " + user + "s message due to links disabled.");
						break;
					}
				}
			}
			
			// Blacklisted words check
			if(!Twitch.isModerator(e.getTags())) {
				for (String arg : args) {
					if (BlackList.blockedPhrases.contains(arg)) {
						Twitch.chat("/timeout " + user + " 3 Used a blacklisted word/phrase");
						Twitch.chat(user + " You're using a blacklisted word/phrase! (1s)");

						System.out.println("Removed " + user + "s message due to blacklisted word.");
						return;
					}
				}
			}
			
			// Used a /me prefix
			if(!Twitch.settings.get("allowMe") && !Twitch.isModerator(e.getTags())) {
				if(args[0].equalsIgnoreCase("ACTION")) {
					Twitch.chat("/timeout " + user + " 3 Not allowed to use /me");
					Twitch.chat(user + " you're not allowed to use /me (1s)");
					return;
				}
			}
		}
		
		// Check to see if command is in cooldown!
		if(CommandTimer.isInCooldown(args[0]) && !Twitch.isModerator(e.getTags()))
			return;
		
		if(args[0].equalsIgnoreCase("!clip")) {
			if(!Twitch.isStreamLive) {
				Twitch.chat("@" + user + " Tejbz is offline, there's nothing to clip!");
				return;
			}
			
			if(clipTime > 0) // Clip cooldown
				return;
			
			CreateClipList clipData = Twitch.twitchClient.getHelix().createClip(Identity.getAccessToken(Twitch.OAuth2), "25622462", false).execute();
			String clipLink = "https://clips.twitch.tv/" + clipData.getData().get(0).getId();
			Twitch.chat("@" + user + " " + clipLink);

			EmbedBuilder msg = new EmbedBuilder();
			msg.setAuthor(user, "https://twitch.tv/" + user, Twitch.getUser(user).getProfileImageUrl());
			msg.setDescription("Just clipped Tejbz stream. Check it out!\n" + clipLink);
			
			App.twitchLog.sendMessage(msg.build()).queue();
			
			clipTimer();
			return;
		}
		
		if(args[0].equalsIgnoreCase("!commands")) {
			if(args.length == 1) {
				Twitch.chat("@" + user + " List of commands are available at: http://pgda.xyz/commands");

				CommandTimer.addToCooldown(args[0]);
				
				return;
			}
		}
		
		if(args[0].equalsIgnoreCase("!vanish")) {
			if(Twitch.isModerator(e.getTags()))
				return;
			
			Twitch.chat("/timeout " + user + " 1");
			Twitch.chat(user + " Disappeared into the mist...");
			
			CommandTimer.addToCooldown(args[0]);
			return;
		}
		
		if(Twitch.isModerator(e.getTags())) {
			if(args[0].equalsIgnoreCase("!commands")) {
				if(args.length >= 3) {
					
					String setting = args[1];
					
					if(setting.equalsIgnoreCase("add")) {
						if(args.length < 4) {
							Twitch.chat(user + " No reply specified.");
							return;
						}
						
						String cmdName = args[2];
						StringBuilder msg = new StringBuilder();
						for(int i = 3; i < args.length; i++) {
							msg.append(" ").append(args[i]);
						}
						
						if(sql.getCommands().contains(cmdName)) {
							Twitch.chat("@" + user + " The command " + cmdName + " already exists. Did you mean to use !editcmd maybe?");
							return;
						}
						
						if(sql.addCommand(cmdName, msg.toString())) {
							Twitch.chat("@" + user + " Command " + cmdName + " stored!");
							Twitch.commands.put(cmdName, msg.toString());
							WebLog.addToWeblog("TWITCH", user, "Created the command <code>" + cmdName + "</code>");
						}
						
						return;
					}
					
					if(setting.equalsIgnoreCase("edit")) {
						if(args.length < 4) {
							Twitch.chat(user + " No new reply specified.");
							return;
						}
						
						String cmdName = args[2];
						StringBuilder msg = new StringBuilder();
						for(int i = 3; i < args.length; i++) {
							msg.append(" ").append(args[i]);
						}
						
						if(!sql.getCommands().contains(cmdName)) {
							Twitch.chat("@" + user + " There is no command named " + cmdName);
							return;
						}
						
						if(sql.editCommand(cmdName, msg.toString())) {
							Twitch.chat("@" + user + " Command " + cmdName + " stored!");
							Twitch.commands.replace(cmdName, msg.toString());
							WebLog.addToWeblog("TWITCH", user, "Edited the command <code>" + cmdName + "</code>");
						} else {
							Twitch.chat("@" + user + " Failed editing the command " + cmdName);
						}
						
						return;
					}
					
					if(setting.equalsIgnoreCase("delete")) {
						if(!sql.getCommands().contains(args[2])) {
							Twitch.chat("@" + user + " There is no command named " + args[2]);
							return;
						}
						
						if(sql.deleteCommand(args[2])) {
							Twitch.chat("@" + user + " Command " + args[2] + " successfully deleted!");
							Twitch.commands.remove(args[2]);
							WebLog.addToWeblog("TWITCH", user, "Deleted the command <code>" + args[2] + "</code>");
						}
						
						return;
					}
				}
			}
			if(args[0].equalsIgnoreCase("!automessage")) {
				if(args.length < 3) {
					Twitch.chat("@" + user + " Invalid arguments. Visit http://pgda.xyz/commands for commands list.");
					return;
				}
				
				String setting = args[1];
				
				if(setting.equalsIgnoreCase("interval")) {
					AutoMessageSQL amSql = new AutoMessageSQL();
					
					if(amSql.setInterval(Integer.parseInt(args[2]))) {
						Twitch.chat("@" + user + " Successfully saved the auto-message delay to: " + args[2] + "!");
						WebLog.addToWeblog("TWITCH", user, "Changed the automessage interval to " + args[2]);
						return;
					}
				} else if(setting.equalsIgnoreCase("add")) {
					StringBuilder message = new StringBuilder();
					for(int i = 2; i < args.length; i++) {
						message.append(args[i]).append(" ");
					}
					
					AutoMessageSQL amSql = new AutoMessageSQL();
					
					if(!amSql.addAutoMessage(message.toString())) {
						Twitch.chat("Failed to add/update playlist. Try again later!");
						return;
					}
					
					AutoMessage.updateAutoMessages();
					
					Twitch.chat("Added message and updated playlist.");
					
					WebLog.addToWeblog("TWITCH", user, "Added a message to auto-message: <code>" + message + "</code>");
					return;
				} else if(setting.equalsIgnoreCase("remove")) {
					StringBuilder message = new StringBuilder();
					for(int i = 2; i < args.length; i++) {
						message.append(args[i]).append(" ");
					}
					
					AutoMessageSQL amSql = new AutoMessageSQL();
					
					if(!amSql.removeAutoMessage(message.toString())) {
						Twitch.chat("Failed to remove/update playlist. Try again later!");
						return;
					}
					
					AutoMessage.updateAutoMessages();
					
					Twitch.chat("Removed the message from the playlist!");
					WebLog.addToWeblog("TWITCH", user, "Removed a message from auto-message: <code>" + message + "</code>");
					return;
				}
				
			}
			
			if(args[0].equalsIgnoreCase("!title")) {
				if(args.length < 2) {
					String title = Twitch.getChannelInfo().getTitle();
					Twitch.chat("Current title set to: " + title);
					return;
				}
				
				StringBuilder title = new StringBuilder();
				for(int i = 1; i < args.length; i++) {
					title.append(args[i]).append(" ");
				}
				
				Twitch.setTitle(title.toString());
				
				Twitch.chat("Title set to: " + title);
				
				return;
			}
			
			if(args[0].equalsIgnoreCase("!game")) {
				if(args.length < 2) {
					String game = Twitch.getChannelInfo().getGameName();
					Twitch.chat("Tejbz is currently playing " + game);
					return;
				}
				
				StringBuilder game = new StringBuilder();
				for(int i = 1; i < args.length; i++) {
					if(i == args.length-1)
						game.append(args[i]);
					else
						game.append(args[i]).append(" ");
				}
				
				Twitch.setGame(game.toString());
				
				Twitch.chat("Game set to: " + game);
				
				return;
			}

			if(args[0].equalsIgnoreCase("!ad")) {
				if(!Twitch.isStreamLive) {
					Twitch.chat("Tejbz is not live.");
					return;
				}

				int time = 30;
				if(args.length > 1) {
					try {
						time = Integer.parseInt(args[1]);
					} catch(NumberFormatException ex) {
						Twitch.chat("@" + user + " Invalid argument");
						return;
					}
				}

				if(Twitch.runAd(time))
					Twitch.chat("Running a " + time + " second ad.");
				else
					Twitch.chat("Couldn't start the ad.");
				return;
			}
			
			if(args[0].equalsIgnoreCase("!help")) {
				Twitch.chat("@" + user + " Bot formatting and commands are available over at http://pgda.xyz/commands");
				return;
			}
		}
		
		if(Twitch.commands.containsKey(args[0].toLowerCase())) {
			String reply = Twitch.commands.get(args[0]);
			
			if(Twitch.specCommands.containsKey(args[0])) {
				if(Twitch.specCommands.get(args[0]).equalsIgnoreCase("mod") && !Twitch.isModerator(e.getTags()))
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
				reply = reply.replace("[touser]", (args.length > 1 ? args[1] : user));
			if(reply.contains("[@touser]"))
				reply = reply.replace("[@touser]", (args.length > 1 ? "@" + args[1] : "@" + user));
			if(reply.contains("[recent_video]")) {
				PropertyHandler propertyHandler = new PropertyHandler();
				reply = reply.replace("[recent_video]", propertyHandler.getPropertyValue("recent_video"));
			}

			Twitch.chat(reply);
			CommandSQL.incrementUses(args[0]);

			CommandTimer.addToCooldown(args[0]);
			
			return;
		}
	}
	
	@EventSubscriber
	public void onLive(ChannelGoLiveEvent e) {
		System.out.println("**********************");
		System.out.println("Tejbz went live!");
		System.out.println("**********************");

		Twitch.isStreamLive = true;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("Tejbz", null, Twitch.getUser(e.getChannel().getName()).getProfileImageUrl());
		msg.setColor(App.color);
		msg.setImage(e.getStream().getThumbnailUrl(1280, 720));
		msg.setTitle("Tejbz just went live!", "https://www.twitch.tv/tejbz");
		msg.addField("Title", e.getStream().getTitle(), false);
		msg.addField("Playing", Twitch.getGameById(e.getStream().getGameId()), false);
		
		App.general.sendMessage(App.guild.getPublicRole().getAsMention() + " Tejbz just went live!").queue();
		App.general.sendMessage(msg.build()).queue();

		Twitch.chatMe("Tejbz Just went live! You can now collect your daily PGDA Coins with !collect");
	}

	@EventSubscriber
	public void onOffline(ChannelGoOfflineEvent e) {
		Twitch.isStreamLive = false;
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
			if(months <= 1) {
				Twitch.chatMe("tejbzWave Welcome to the squad, @" + user + " tejbzLove");
				BankHandler.onFirstSub(user);
			} else {
				Twitch.chatMe("pepeD Welcome back @" + user + " pepeD");
				BankHandler.onResub(user);
			}
		
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
			Twitch.chatMe("tejbzPog Thanks for the gifted, @" + user + " tejbzLove");
		
		TextChannel channel = App.twitchLog;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(App.color);
		msg.setAuthor(user, "https://twitch.tv/" + user, Twitch.getUser(user).getProfileImageUrl());
		msg.setDescription("Just gifted " + count + " subs to the community! They've gifted a total of " + totalGifted + " subs.");
		
		channel.sendMessage(msg.build()).queue();
	}

	@EventSubscriber
	public void onChannelPoints(RewardRedeemedEvent e) {
		String rewardId = e.getRedemption().getReward().getId();
		String user = e.getRedemption().getUser().getDisplayName();

		if(rewardId.equalsIgnoreCase("6b82416f-1197-4e92-b787-486967de076a")) {
			Twitch.chatMe(user + " WENT ZOOM ZOOM ZOOM");
			return;
		}

		// Giveaway Reward
		if(rewardId.equalsIgnoreCase("2d4c2121-f036-4521-8060-9af23f53dadf")) {
			Giveaway giveaway = new Giveaway();

			if(giveaway.addToGiveawayList(e.getRedemption().getUser().getDisplayName(), Twitch.isSubscribed(e.getRedemption().getUser().getDisplayName())))
				Twitch.sendPm(user, "You're entered in the giveaway!");

			return;
		}
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
	
}