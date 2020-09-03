package com.jonteohr.tejbz.credentials;

import java.util.TimerTask;

import com.jonteohr.tejbz.twitch.Twitch;

public class RefreshToken extends TimerTask {

	@Override
	public void run() {
		Identity identity = new Identity();
		
		identity.refreshToken(Twitch.OAuth2);
	}

}
