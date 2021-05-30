package com.github.jonteohr.tejbz.discord.listener.guild;

import com.github.jonteohr.tejbz.App;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleRequest extends ListenerAdapter {
	public static TextChannel roleRequest;
	public static Message regionRequestMessage;
	public static Message gameRequestMessage;

	// Region roles
	public static Role roleNA;
	public static Role roleSA;
	public static Role roleEU;
	public static Role roleAS;

	// Game roles
	public static Role gameMc;
	public static Role gameWz;
	public static Role gameRl;
	public static Role gameSot;
	public static Role gameMisc;

	public static void sendMsg() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(App.color);
		eb.setTitle("__Role Menu: Game Roles__");
		eb.setDescription("React to one of the emotes below to get the specified role.");

		roleRequest.sendMessage(eb.build()).queue();
	}

	public static void editmsg() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("__Role Menu: Game Roles__");
		eb.setDescription("React to one of the emotes below to get the specified role.\n\n" +
				"<:mc:808961323299241994> : `Minecraft`\n\n" +
				"<:warzone:808962059647582249> : `COD: Warzone`\n\n" +
				"<:rl:808962304699531295> : `Rocket League`\n\n" +
				"<:sot:808962592486719498> : `Sea of Thieves`\n\n" +
				"\uD83D\uDD79 : `Other Games`");
		eb.setColor(App.color);
		eb.setAuthor(null);

		gameRequestMessage.editMessage(eb.build()).queue();

		react();
	}

	private static void react() {
		gameRequestMessage.addReaction("mc:808961323299241994").queue();
		gameRequestMessage.addReaction("warzone:808962059647582249").queue();
		gameRequestMessage.addReaction("rl:808962304699531295").queue();
		gameRequestMessage.addReaction("sot:808962592486719498").queue();
		gameRequestMessage.addReaction("\uD83D\uDD79").queue();
	}

	/*
		Region roles
	 */
	public void onMessageReactionAdd(MessageReactionAddEvent e) {
		if(!e.getChannel().getId().equalsIgnoreCase("808783594020798514"))
			return;
		Guild guild = e.getGuild();
		Member member = e.getMember();
		String code = e.getReactionEmote().getAsReactionCode();

		// Region roles
		if(e.getMessageId().equalsIgnoreCase(regionRequestMessage.getId())) {
			if(code.equalsIgnoreCase("rg_na:808954558956175391")) {
				guild.removeRoleFromMember(member, roleEU).complete();
				guild.removeRoleFromMember(member, roleSA).complete();
				guild.removeRoleFromMember(member, roleAS).complete();
				guild.addRoleToMember(member, roleNA).complete();
			} else if(code.equalsIgnoreCase("rg_sa:808954558927470634")) {
				guild.removeRoleFromMember(member, roleEU).complete();
				guild.removeRoleFromMember(member, roleNA).complete();
				guild.removeRoleFromMember(member, roleAS).complete();
				guild.addRoleToMember(member, roleSA).complete();
			} else if(code.equalsIgnoreCase("rg_eu:808954558876876810")) {
				guild.removeRoleFromMember(member, roleNA).complete();
				guild.removeRoleFromMember(member, roleSA).complete();
				guild.removeRoleFromMember(member, roleAS).complete();
				guild.addRoleToMember(member, roleEU).complete();
			} else if(code.equalsIgnoreCase("rg_as:808954558448926742")) {
				guild.removeRoleFromMember(member, roleEU).complete();
				guild.removeRoleFromMember(member, roleSA).complete();
				guild.removeRoleFromMember(member, roleNA).complete();
				guild.addRoleToMember(member, roleAS).complete();
			}
		}

		// Game roles
		else if(e.getMessageId().equalsIgnoreCase(gameRequestMessage.getId())) {
			if(code.equalsIgnoreCase("mc:808961323299241994")) {
				guild.addRoleToMember(member, gameMc).complete();
			} else if(code.equalsIgnoreCase("warzone:808962059647582249")) {
				guild.addRoleToMember(member, gameWz).complete();
			} else if(code.equalsIgnoreCase("rl:808962304699531295")) {
				guild.addRoleToMember(member, gameRl).complete();
			} else if(code.equalsIgnoreCase("sot:808962592486719498")) {
				guild.addRoleToMember(member, gameSot).complete();
			} else if(code.equalsIgnoreCase("\uD83D\uDD79")) {
				guild.addRoleToMember(member, gameMisc).complete();
			}
		}
	}

	public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
		Guild guild = e.getGuild();
		Member member = e.getMember();
		String code = e.getReactionEmote().getAsReactionCode();

		// Game roles
		if(e.getMessageId().equalsIgnoreCase(gameRequestMessage.getId())) {
			if(code.equalsIgnoreCase("mc:808961323299241994")) {
				guild.removeRoleFromMember(member, gameMc).complete();
			} else if(code.equalsIgnoreCase("warzone:808962059647582249")) {
				guild.removeRoleFromMember(member, gameWz).complete();
			} else if(code.equalsIgnoreCase("rl:808962304699531295")) {
				guild.removeRoleFromMember(member, gameRl).complete();
			} else if(code.equalsIgnoreCase("sot:808962592486719498")) {
				guild.removeRoleFromMember(member, gameSot).complete();
			} else if(code.equalsIgnoreCase("\uD83D\uDD79")) {
				guild.removeRoleFromMember(member, gameMisc).complete();
			}
		}
	}
}
