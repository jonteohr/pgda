package com.github.jonteohr.tejbz.discord.listener.guild;

import com.github.jonteohr.tejbz.App;
import com.github.jonteohr.tejbz.discord.listener.commands.Join;
import com.github.jonteohr.tejbz.discord.listener.roles.AutomaticRoles;
import com.github.jonteohr.tejbz.discord.listener.roles.DefaultRoles;
import com.github.jonteohr.tejbz.discord.queue.WaitingQueue;
import com.github.jonteohr.tejbz.twitch.Twitch;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class GuildReady extends ListenerAdapter {

	public void onGuildReady(GuildReadyEvent e) {
		Twitch.initTwitch();

		App.guild = e.getGuild();
		App.general = e.getGuild().getTextChannelById("124204242683559938");
		App.twitchLog = e.getGuild().getTextChannelById("735122823017791578");
		Join.lobby = App.guild.getVoiceChannelById("124204246815080449");
		Join.queue = App.guild.getVoiceChannelById("732569438326489139");
		Join.live = App.guild.getVoiceChannelById("280730003484835860");

		setPresence(Twitch.getSubscribers("tejbz"));

		presenceTimer();

		// Set the role variables
		DefaultRoles.setRoles(e.getGuild());

		// Role requests
		RoleRequest.roleRequest = e.getGuild().getTextChannelById("808783594020798514");
		RoleRequest.regionRequestMessage = RoleRequest.roleRequest.retrieveMessageById("808948588360106044").complete();
		RoleRequest.gameRequestMessage = RoleRequest.roleRequest.retrieveMessageById("808960926992039937").complete();
		RoleRequest.roleEU = e.getGuild().getRoleById("808958342277496842");
		RoleRequest.roleNA = e.getGuild().getRoleById("808958423618027530");
		RoleRequest.roleSA = e.getGuild().getRoleById("808958497784987688");
		RoleRequest.roleAS = e.getGuild().getRoleById("808958387696959550");
		RoleRequest.gameMc = e.getGuild().getRoleById("808964559292858378");
		RoleRequest.gameMisc = e.getGuild().getRoleById("808964641312210964");
		RoleRequest.gameRl = e.getGuild().getRoleById("808964875396841483");
		RoleRequest.gameSot = e.getGuild().getRoleById("808964794341916674");
		RoleRequest.gameWz = e.getGuild().getRoleById("808964608579731485");

//		RoleRequest.editmsg();
//		RoleRequest.sendMsg();

		// Waiting queue
		WaitingQueue.infoChannel = e.getGuild().getTextChannelById("808994821866782740");
		WaitingQueue.waitingRoom = e.getGuild().getVoiceChannelById("808994976431997030");
		WaitingQueue.supporterRoom = e.getGuild().getVoiceChannelById("808995441428660254");
		WaitingQueue.queueMessage = WaitingQueue.infoChannel.retrieveMessageById("809003922072535051").complete();

		WaitingQueue.updateQueue(e.getGuild());
		WaitingQueue.expirationTimer();
    
		// Member roles
		AutomaticRoles.memberRole = e.getGuild().getRoleById("809140872733786152");
		AutomaticRoles.veteranRole = e.getGuild().getRoleById("809148853751250945");
		AutomaticRoles.veteranDivider = e.getGuild().getRoleById("809148571650752614");
		AutomaticRoles.twelveMonths = e.getGuild().getRoleById("809148668183052309");
		AutomaticRoles.eighteenMonths = e.getGuild().getRoleById("809148919786242058");
		AutomaticRoles.twentyfourMonths = e.getGuild().getRoleById("809148966780403793");
		AutomaticRoles.checkMemberStatus();
	}

	/**
	 *
	 * @param subs a {@link java.lang.Integer Integer} of total member count.
	 */
	public void setPresence(int subs) {
		App.jda.getPresence().setActivity(Activity.watching(subs + " subs | Twitch.tv/Tejbz"));
	}

	public void presenceTimer() {
		Timer timer = new Timer();



		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				setPresence(Twitch.getSubscribers("tejbz"));
			}
		}, 10*60*1000, 10*60*1000);
	}
}
