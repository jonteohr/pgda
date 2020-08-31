package com.jonteohr.tejbz.credentials;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.TwitchIdentityProvider;

public class Identity {
	private final TwitchIdentityProvider identityProvider = new TwitchIdentityProvider("client_id", "client_secret", "redirect_url");
	
	public OAuth2Credential refreshToken(OAuth2Credential credential) {
		return identityProvider.refreshCredential(credential).get();
	}
}
