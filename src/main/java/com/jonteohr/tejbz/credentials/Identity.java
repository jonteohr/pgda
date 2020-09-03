package com.jonteohr.tejbz.credentials;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.jonteohr.tejbz.PropertyHandler;

public class Identity {
	private static final TwitchIdentityProvider identityProvider = new TwitchIdentityProvider("client_id", "client_secret", "redirect_uri");
	
	public OAuth2Credential refreshToken(OAuth2Credential credential) {
		OAuth2Credential newOauth = identityProvider.refreshCredential(getCredential(credential)).get();
		
		PropertyHandler props = new PropertyHandler();
		props.setProperty("access_token", newOauth.getAccessToken());
		props.setProperty("refresh_token", newOauth.getRefreshToken());
		
		// revoke the old one
		// okhttp3 is outdated, we need 4.6.0
//		identityProvider.revokeCredential(getCredential(credential));
		
		return newOauth;
	}
	
	public OAuth2Credential getCredential(OAuth2Credential credential) {
		return identityProvider.getAdditionalCredentialInformation(credential).get();
	}
	
	public static String getAccessToken(OAuth2Credential credential) {
		Identity identity = new Identity();
		OAuth2Credential oauth = identity.getCredential(credential);
		
		return oauth.getAccessToken();
	}
}
