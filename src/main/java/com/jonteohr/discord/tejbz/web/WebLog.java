package com.jonteohr.discord.tejbz.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

import com.jonteohr.discord.tejbz.App;
import com.jonteohr.discord.tejbz.credentials.Credentials;

public class WebLog {
	
	/**
	 * Adds an event to the dashboard logs.
	 * @param moderator
	 * @param action
	 */
	public static boolean addToWeblog(String type, String moderator, String action) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
						Credentials.DB_USER.getValue(),
						Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO logs(date,type,moderator,action) VALUES (?,?,?,?)");
			pstmt.setString(1, App.sdf.format(new Date()));
			pstmt.setString(2, type);
			pstmt.setString(3, moderator);
			pstmt.setString(4, action);
			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
