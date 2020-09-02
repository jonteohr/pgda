package com.jonteohr.tejbz.credentials;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.TwitchIdentityProvider;
import com.jonteohr.tejbz.PropertyHandler;
import com.jonteohr.tejbz.twitch.Twitch;

public class Identity {
	private static final TwitchIdentityProvider identityProvider = new TwitchIdentityProvider("client_id", "client_secret", "redirect_uri");
	
	public OAuth2Credential refreshToken(OAuth2Credential credential) {
		OAuth2Credential newOauth = identityProvider.refreshCredential(getCredential(credential)).get();
		
		PropertyHandler props = new PropertyHandler();
		props.setProperty("access_token", newOauth.getAccessToken());
		props.setProperty("refresh_token", newOauth.getRefreshToken());
		
		return newOauth;
	}
	
	public OAuth2Credential getCredential(OAuth2Credential credential) {
		CredentialManager cred = CredentialManagerBuilder.builder().build();
		
		cred.registerIdentityProvider(identityProvider);
		cred.addCredential("twitch", credential);
		
		return cred.getOAuth2CredentialByUserId(Twitch.getUser("tejbz").getId()).get();
	}
	
	public static String getAccessToken(OAuth2Credential credential) {
		Identity identity = new Identity();
		OAuth2Credential oauth = identity.getCredential(credential);
		
		return oauth.getAccessToken();
	}
}
