package com.github.condolent.tejbz.twitch;

import com.github.condolent.tejbz.twitch.sql.BankSQL;
import com.github.condolent.tejbz.twitch.threads.CoinsTimer;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class BankHandler {

	private final int minDaily = 500;
	private final int maxDaily = 1000;

	@EventSubscriber
	public void onDaily(IRCMessageEvent e) {
		if(!e.getChannel().getName().equalsIgnoreCase("tejbz"))
			return;

		if(!e.getMessage().isPresent() || e.getUser() == null)
			return;

		String[] args = e.getMessage().get().split("\\s+");
		String user = e.getTags().get("display-name");

		if(!args[0].equalsIgnoreCase("!bank")
		&& !args[0].equalsIgnoreCase("!collect")
		&& !args[0].equalsIgnoreCase("!roll")
		&& !args[0].equalsIgnoreCase("!givecoins")
		&& !args[0].equalsIgnoreCase("!transfer")
		&& !args[0].equalsIgnoreCase("!dropcoins"))
			return;

		BankSQL bankSQL = new BankSQL();

		if(args[0].equalsIgnoreCase("!bank")) {
			int coins = bankSQL.getCoins(user);
			String fCoins = String.format("%,d", coins);

			Twitch.sendPm(user, "You currently have " + fCoins + " PGDA coins.");
			return;
		}

		/*
			MODERATOR
			COMMANDS
		 */
		if(Twitch.isModerator(e.getTags())) {
			if(args[0].equalsIgnoreCase("!givecoins")) {
				if(args.length < 3) {
					Twitch.sendPm(user, "Correct usage: !givecoins [user] [amount]");
					return;
				}

				User target = Twitch.getUser(args[1]);
				if(target == null) {
					Twitch.sendPm(user, "Couldn't find user " + args[1]);
					return;
				}

				if(!bankSQL.isUserInDatabase(target.getDisplayName())) {
					Twitch.sendPm(user, target.getDisplayName() + " has never collected any coins. They must have done this at least once!");
					return;
				}

				try {
					int amount = Integer.parseInt(args[2]);
					bankSQL.incrementCoins(target.getDisplayName(), amount);
					Twitch.sendPm(user, "You've awarded " + target.getDisplayName() + " " + amount + " PGDA Coins.");
					Twitch.sendPm(target.getDisplayName(), user + " has awarded you " + amount + " PGDA Coins.");
					return;
				} catch(NumberFormatException ex) {
					Twitch.sendPm(user, args[2] + " is not a valid number.");
					return;
				}
			}

			if(args[0].equalsIgnoreCase("!dropcoins") && (user.equalsIgnoreCase("tejbz") || user.equalsIgnoreCase("rlhypr"))) {
				int coins;

				if(args.length < 2) {
					coins = 100;
				} else {
					try {
						coins = Integer.parseInt(args[1]);
					} catch (NumberFormatException ex) {
						ex.printStackTrace();
						Twitch.chat(user + " " + args[1] + " is not a valid number.");
						return;
					}
				}

				List<String> chatters = Twitch.twitchClient.getMessagingInterface().getChatters("tejbz").execute().getAllViewers();
				int finalCoins = coins;
				chatters.forEach(chatter -> {
					if(bankSQL.isUserInDatabase(chatter))
						bankSQL.incrementCoins(chatter, finalCoins);
				});

				Twitch.chatMe("tejbzBeer " + user + " Just awarded all viewers with " + coins + " PGDA Coins! tejbzPog");
				return;
			}
		}

		/*
			REGULAR
			COMMANDS
		 */

		if(args[0].equalsIgnoreCase("!collect")) {
			Random random = new Random();
			int coins = random.nextInt(maxDaily - minDaily) + minDaily;

			String fCoins = String.format("%,d", coins);
			if(!bankSQL.isUserInDatabase(user)) {
				if(bankSQL.collectDaily(user, coins)) {
					Twitch.sendPm(user, "You've collected your daily " + fCoins + " PGDA coins!");
				}
				return;
			}

			if(bankSQL.getLastCollected(user) == null) {
				if(bankSQL.collectDaily(user, coins))
					Twitch.sendPm(user, "You've collected your daily " + fCoins + " PGDA coins!");

				return;
			}

			Calendar current = Calendar.getInstance();
			current.set(Calendar.HOUR_OF_DAY, 0);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			current.set(Calendar.MILLISECOND, 0);
			Calendar last = Calendar.getInstance();
			last.setTime(bankSQL.getLastCollected(user));
			last.set(Calendar.HOUR_OF_DAY, 0);
			last.set(Calendar.MINUTE, 0);
			last.set(Calendar.SECOND, 0);
			last.set(Calendar.MILLISECOND, 0);

			if(last.compareTo(current) < 0) {
				if(bankSQL.collectDaily(user, coins)) {
					Twitch.sendPm(user, "You've collected your daily " + fCoins + " PGDA coins!");
				}
			} else {
				Twitch.sendPm(user, "You've already collected your daily PGDA Coins!");
			}

			return;
		}

		if(args[0].equalsIgnoreCase("!roll")) {
			if(CoinsTimer.isCooldown(user)) {
				Twitch.sendPm(user, "That command is currently in a cooldown! Try again in " + CoinsTimer.getCooldown(user) + " minutes.");
				return;
			}

			if(args.length < 2) {
				Twitch.chat("@" + user + " You didn't specify a bet amount.");
				return;
			}

			try {
				int bet = Integer.parseInt(args[1]);

				rollCoins(user, bet);
				return;

			} catch(NumberFormatException ex) {
				if(!args[1].equalsIgnoreCase("all")) {
					Twitch.chat("@" + user + " Bet amount was not a valid number.");
					return;
				}

				rollCoins(user, bankSQL.getCoins(user));
				return;
			}
		}

		if(args[0].equalsIgnoreCase("!transfer")) {
			if(args.length < 3) {
				Twitch.sendPm(user, "Invalid arguments. Need to specify who you want to give coins and how much. Like this: !transfer rlHypr 1337");
				return;
			}

			try {
				User target = Twitch.getUser(args[1]);
				int amount = Integer.parseInt(args[2]);

				if(target == null) {
					Twitch.sendPm(user, "Could not find user " + args[1]);
					return;
				}

				if(bankSQL.getCoins(user) < amount) {
					Twitch.sendPm(user, "You don't have " + amount + " coins.");
					return;
				}

				if(!bankSQL.incrementCoins(target.getDisplayName(), amount) && bankSQL.decrementCoins(user, amount)) {
					Twitch.sendPm(user, "Couldn't transfer at the moment. Please try again later!");
					return;
				}

				Twitch.sendPm(user, "You've given " + target + " " + amount + " Coins!");
				Twitch.sendPm(target.getDisplayName(), user + " has given you " + amount + " Coins!");

			} catch(NumberFormatException ex) {
				Twitch.sendPm(user, args[2] + " is not a valid number.");
				return;
			}

		}
	}

	@EventSubscriber
	public void onRewardRedeemed(RewardRedeemedEvent e) {
		String rewardId = e.getRedemption().getReward().getId();
		String user = e.getRedemption().getUser().getDisplayName();
		long cost = e.getRedemption().getReward().getCost();
		int coins = 2000;

		if(rewardId.equalsIgnoreCase("dcaa91f7-7e6f-4746-b081-9e444a010def")) {
			BankSQL bankSQL = new BankSQL();

			bankSQL.incrementCoins(user, coins);
			Twitch.sendPm(user, "You just bought " + String.format("%,d", coins) + " PGDA Coins for " + String.format("%,d", cost) + " Channel Points.");
		}
	}

	public static void onFirstSub(String user) {
		BankSQL bankSQL = new BankSQL();

		Random random = new Random();
		int coins = random.nextInt(2000 - 1000) + 1000;

		if(bankSQL.incrementCoins(user, coins))
			Twitch.sendPm(user, "You've received " + coins + " PGDA Coins for subscribing!");
	}

	public static void onResub(String user) {
		BankSQL bankSQL = new BankSQL();

		Random random = new Random();
		int coins = random.nextInt(1500 - 1000) + 1000;

		if(bankSQL.incrementCoins(user, coins))
			Twitch.sendPm(user, "You've received " + coins + " PGDA Coins for re-subbing!");
	}

	private void rollCoins(String user, int bet) {
		BankSQL bankSQL = new BankSQL();

		if(bet < 100) {
			Twitch.sendPm(user, "Bet needs to be at least 100 PGDA coins.");
			return;
		}
		if(bankSQL.getCoins(user) < bet) {
			Twitch.chat("@" + user + " You don't have enough PGDA coins.");
			return;
		}

		int returned = (int) Math.round(bet*0.75);

		Random random = new Random();
		double r = random.nextDouble();
		boolean result;

		result = r <= 0.5;

		if(result) {
			if(bankSQL.incrementCoins(user, returned)) {
				CoinsTimer.activateCooldown(user);
				Twitch.chatMe(user + " Rolled and won " + (returned + bet) + " PGDA Coins, with a profit of " + returned + ".");
			} else
				Twitch.chat("@" + user + " Error while rolling. Please try again!");

		} else {
			if(bankSQL.decrementCoins(user, bet)) {
				CoinsTimer.activateCooldown(user);
				Twitch.chat("@" + user + " You lost " + bet + " PGDA Coins.");
			} else
				Twitch.chat("@" + user + " Error while rolling. Please try again!");

		}
	}

}
