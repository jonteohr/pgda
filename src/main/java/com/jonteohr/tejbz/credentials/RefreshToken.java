package com.jonteohr.tejbz.credentials;

import java.util.TimerTask;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.jonteohr.tejbz.twitch.Twitch;

public class RefreshToken extends TimerTask {

	@Override
	public void run() {
		Identity identity = new Identity();
		
		OAuth2Credential newOauth = identity.refreshToken(Twitch.OAuth2);
		
		Twitch.OAuth2 = identity.getCredential(newOauth);
	}

}
