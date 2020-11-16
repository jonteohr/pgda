package com.github.condolent.tejbz.credentials;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.condolent.tejbz.PropertyHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Identity {
	private static final TwitchIdentityProvider identityProvider = new TwitchIdentityProvider("client_id", "client_secret", "redirect_uri");
	
	public OAuth2Credential refreshToken(OAuth2Credential credential) {
		OAuth2Credential newOauth = identityProvider.refreshCredential(getCredential(credential)).get();
		
		PropertyHandler props = new PropertyHandler();
		props.setProperty("access_token", newOauth.getAccessToken());
		props.setProperty("refresh_token", newOauth.getRefreshToken());
		
		// revoke the old one for safety
		identityProvider.revokeCredential(getCredential(credential));

		saveOAuth(props.getPropertyValue("access_token"), props.getPropertyValue("refresh_token"));
		
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

	private boolean saveOAuth(String access, String refresh) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO oauth(user,access,refresh) VALUES(?,?,?);");
			pstmt.setString(1, "pgdabot");
			pstmt.setString(2, access);
			pstmt.setString(3, refresh);
			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
