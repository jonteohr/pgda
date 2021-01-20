package com.github.condolent.tejbz.credentials;

import java.util.TimerTask;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.condolent.tejbz.twitch.Twitch;

public class RefreshToken extends TimerTask {

	@Override
	public void run() {
		OAuth2Credential oldOauth = Twitch.getOAuth2();

		Twitch.setOAuth2(Identity.getCredential(Identity.refreshToken(oldOauth)));
		System.out.println("###########");
		System.out.println("Refreshed token!");
		System.out.println("###########");
	}

}
