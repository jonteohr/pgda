package com.github.condolent.tejbz.twitch.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.github.condolent.tejbz.credentials.Credentials;

public class BlackList {
	
	public static List<String> blockedPhrases = new ArrayList<>();
	
	public List<String> getBlacklist() {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			Statement pstmt = con.createStatement();
			result = pstmt.executeQuery("SELECT * FROM blacklist;");

			List<String> blacklist = new ArrayList<>();

			while (result.next()) {
				blacklist.add(result.getString(1));
			}
			
			result.close();
			con.close();

			return blacklist;

		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
