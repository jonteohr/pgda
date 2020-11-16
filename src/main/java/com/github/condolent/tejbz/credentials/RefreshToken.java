package com.github.condolent.tejbz.credentials;

import java.util.TimerTask;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.condolent.tejbz.twitch.Twitch;

public class RefreshToken extends TimerTask {

	@Override
	public void run() {
		Identity identity = new Identity();
		
		OAuth2Credential newOauth = identity.refreshToken(Twitch.OAuth2);
		
		Twitch.OAuth2 = identity.getCredential(newOauth);
	}

}
