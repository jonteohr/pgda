package com.github.jonteohr.tejbz.twitch.giveaway;

import com.github.jonteohr.tejbz.twitch.Twitch;
import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.chat.events.channel.IRCMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GiveawayCommand {

	private static boolean giveawayActive = false;
	private static String keyword;
	private static List<String> participants = new ArrayList<>();

	@EventSubscriber
	public void onCommand(IRCMessageEvent e) {
		if(!e.getChannel().getName().equalsIgnoreCase("tejbz"))
			return;

		if(!e.getMessage().isPresent() || e.getUser() == null)
			return;

		if(!Twitch.isModerator(e.getTags()))
			return;

		String[] args = e.getMessage().get().split("\\s+");

		if(args[0].equalsIgnoreCase("!raffle")) {
			if(giveawayActive) { // Ignore if already active
				Twitch.chat("A raffle is already active. Type !draw to draw a winner.");
				return;
			}

			giveawayActive = true;

			if(args.length < 2) {
				Twitch.chatMe("A raffle has started! Type anything in chat to be a part of it and have a chance of winning!");
				return;
			}

			keyword = args[1];
			Twitch.chatMe("A raffle has started! To enter type this keyword in chat: " + keyword);
			return;
		}

		if(args[0].equalsIgnoreCase("!draw")) {
			if(!giveawayActive) {
				Twitch.chat("A raffle is not active at the moment.");
				return;
			}

			if(participants.size() == 0) {
				Twitch.chat("No users participated in the raffle. There is no winner!");
				clearRoll();
				return;
			}

			int total = participants.size();
			int winner = new Random().nextInt(total);

			Twitch.chatMe("We have a winner! Congratulations @" + participants.get(winner) + "!");

			clearRoll();
		}
	}

	@EventSubscriber
	public void onParticipate(IRCMessageEvent e) {
		if(!e.getChannel().getName().equalsIgnoreCase("tejbz"))
			return;

		if(!e.getMessage().isPresent() || e.getUser() == null)
			return;

		if(e.getUserName().equalsIgnoreCase("tejbz"))
			return;

		if(!giveawayActive)
			return;

		if(participants.contains(e.getUserName()))
			return;

		if(keyword == null) {
			participants.add(e.getUserName());
			return;
		}

		if(e.getMessage().get().contains(keyword)) {
			participants.add(e.getUserName());
			return;
		}
	}

	private void clearRoll() {
		// Clear the giveaway settings
		keyword = null;
		participants.clear();
		giveawayActive = false;
	}

}
