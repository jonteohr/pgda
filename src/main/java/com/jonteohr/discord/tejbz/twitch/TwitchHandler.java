package com.jonteohr.discord.tejbz.twitch;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.GiftSubscriptionsEvent;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.CreateClipList;
import com.github.twitch4j.pubsub.events.ChannelSubscribeEvent;
import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.PropertyHandler;
import com.jonteohr.discord.tejbz.sql.AutoMessageSQL;
import com.jonteohr.discord.tejbz.sql.CommandSQL;
import com.jonteohr.discord.tejbz.twitch.automessage.AutoMessage;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class TwitchHandler {
	
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
		
		if(args[0].equalsIgnoreCase("!clip")) {
			if(Twitch.getStream("tejbz") == null) {
				chat("@" + user + " Tejbz is offline, there's nothing to clip!");
				return;
			}
			CreateClipList clipData = Twitch.twitchClient.getHelix().createClip(Twitch.OAuth2.getAccessToken(), "25622462", false).execute();
			String clipLink = "https://clips.twitch.tv/" + clipData.getData().get(0).getId();
			chat("@" + user + " " + clipLink);

			EmbedBuilder msg = new EmbedBuilder();
			msg.setAuthor(user, null, Twitch.getUser(user).getProfileImageUrl());
			msg.setDescription("(" + user + ")[https://twitch.tv/" + user + "] clipped Tejbz stream. Check it out!\n" + clipLink);
			
			App.twitchLog.sendMessage(msg.build()).queue();
			return;
		}
		
		if(args[0].equalsIgnoreCase("!commands")) {
			chat("@" + user + " List of commands are available at: http://plox.nu/tejbz");
			return;
		}
		
		if(Twitch.commands.containsKey(args[0])) {
			String reply = Twitch.commands.get(args[0])
					.replace("[@user]", "@" + user)
					.replace("[user]", user);
			
			if(reply.contains("[subcount]"))
				reply = reply.replace("[subcount]", Twitch.getSubscribers("tejbz") + "");
			if(reply.contains("[follows]"))
				reply = reply.replace("[follows]", "" + Twitch.getFollowers("tejbz"));
			if(reply.contains("[count]"))
				reply = reply.replace("[count]", String.valueOf(CommandSQL.getUses(args[0])));
			
			chat(reply);
			CommandSQL.incrementUses(args[0]);
			return;
		}
		
		if(e.getTags().containsKey("badges")) {
			if(e.getTags().get("badges").contains("broadcaster") || e.getTags().get("badges").contains("moderator")) {
				if(args[0].equalsIgnoreCase("!addcmd")) {
					if(args.length < 3) {
						chat("@" + user + " Not enough arguments.. Correct usage: !addcmd <!commandName> <reply>");
						return;
					}
					String cmdName = args[1];
					String msg = "";
					for(int i = 2; i < args.length; i++) {
						msg = msg + " " + args[i];
					}
					
					if(sql.getCommands().contains(cmdName)) {
						chat("@" + user + " The command " + cmdName + " already exists. Did you mean to use !editcmd maybe?");
						return;
					}
					
					if(sql.addCommand(cmdName, msg)) {
						chat("@" + user + " Command " + cmdName + " stored!");
						Twitch.commands.put(cmdName, msg);
					}
					
					return;
				}
				
				if(args[0].equalsIgnoreCase("!editcmd")) {
					if(args.length < 3) {
						chat("@" + user + " Not enough arguments.. Correct usage: !addcmd <!commandName> <reply>");
						return;
					}
					
					String cmdName = args[1];
					String msg = "";
					for(int i = 2; i < args.length; i++) {
						msg = msg + " " + args[i];
					}
					
					if(!sql.getCommands().contains(cmdName)) {
						chat("@" + user + " There is no command named " + cmdName);
						return;
					}
					
					if(sql.editCommand(cmdName, msg)) {
						chat("@" + user + " Command " + cmdName + " stored!");
						Twitch.commands.replace(cmdName, msg);
					} else {
						chat("@" + user + " Failed editing the command " + cmdName);
					}
					
					return;
				}
				
				if(args[0].equalsIgnoreCase("!delcmd")) {
					if(args.length < 2) {
						chat("@" + user + " No command specified. Correct usage: !delcmd <!commandName>");
						return;
					}
					
					if(!sql.getCommands().contains(args[1])) {
						chat("@" + user + " There is no command named " + args[1]);
						return;
					}
					
					if(sql.deleteCommand(args[1])) {
						chat("@" + user + " Command " + args[1] + " successfully deleted!");
						Twitch.commands.remove(args[1]);
					}
					
					return;
				}
				
				if(args[0].equalsIgnoreCase("!automessage")) {
					if(args.length < 3) {
						chat("@" + user + " Invalid arguments. Visit http://plox.nu/tejbz for commands list.");
						return;
					}
					
					String setting = args[1];
					
					if(setting.equalsIgnoreCase("interval")) {
						PropertyHandler props = new PropertyHandler();
						
						if(props.setProperty("automessage_delay", args[2])) {
							chat("@" + user + " Successfully saved the auto-message delay to: " + args[2] + "!");
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
						return;
					}
					
				}
				
				if(args[0].equalsIgnoreCase("!format")) {
					chat("@" + user + " Formatting rules are in the discord: https://discordapp.com/channels/124204242683559938/489590000556441603/732630257861132438");
					return;
				}
			}
		}
	}
	
	@EventSubscriber
	public void onLive(ChannelGoLiveEvent e) {
		System.out.println(e.getStream().getUserName() + " went live.");
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setAuthor("Tejbz", null, Twitch.getUser(e.getChannel().getName()).getProfileImageUrl());
		msg.setColor(App.color);
		msg.setImage(Twitch.getStream("tejbz").getThumbnailUrl(1280, 720));
		msg.setTitle("Tejbz just went live!", "https://www.twitch.tv/tejbz");
		msg.addField("Title", e.getStream().getTitle(), false);
		msg.addField("Playing", Twitch.getGameById(e.getStream().getGameId()), false);
		
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
		
		TextChannel channel = App.twitchLog;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(App.color);
		
		msg.setAuthor(user, "https://twitch.tv/" + user, Twitch.getUser(user).getProfileImageUrl());
		msg.setDescription("Just subscribed with " + tier + ". They've been subscribed for " + months + " months.");
		if(!message.equalsIgnoreCase("No message.."))
			msg.addField("Message", message, true);
		if(streak > 0)
			msg.addField("Streak", streak + " months", true);
		
		channel.sendMessage(msg.build()).queue();
	}
	
	@EventSubscriber
	public void onGiftedSub(GiftSubscriptionsEvent e) {
		String user = e.getUser().getName();
		int count = e.getCount();
		int totalGifted = e.getTotalCount();
		
		// Twitch Chat
		if(Twitch.twitchClient.getChat().isChannelJoined("tejbz"))
			chatMe("tejbzPog Thanks for the gifted, @" + user + " tejbzRage");
		
		TextChannel channel = App.twitchLog;
		
		EmbedBuilder msg = new EmbedBuilder();
		msg.setColor(App.color);
		msg.setAuthor(user, "https://twitch.tv/" + user, Twitch.getUser(user).getProfileImageUrl());
		msg.setDescription("Just gifted " + count + " subs to the community! They've gifted a total of " + totalGifted + " subs.");
		
		channel.sendMessage(msg.build()).queue();
	}
	
	
	/*
	 * TESTING PURPOSES
	 */
	@EventSubscriber
	public void onPubSub(ChannelSubscribeEvent e) {
		System.out.println("PubSub Subscription:");
		System.out.println(e.getData());
		
		App.jda.getUserById("307609343912574976").openPrivateChannel().complete().sendMessage("New Sub!").queue();
		App.jda.getUserById("307609343912574976").openPrivateChannel().complete().sendMessage("" + e.getData()).queue();
	}
	
	private void chat(String msg) {
		Twitch.twitchClient.getChat().sendMessage("tejbz", msg);
	}
	
	private void chatMe(String msg) {
		Twitch.twitchClient.getChat().sendMessage("tejbz", "/me " + msg);
	}
	
}
