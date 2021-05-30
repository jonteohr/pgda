package com.github.jonteohr.tejbz.credentials;

import java.util.TimerTask;

import com.github.jonteohr.tejbz.twitch.Twitch;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

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
