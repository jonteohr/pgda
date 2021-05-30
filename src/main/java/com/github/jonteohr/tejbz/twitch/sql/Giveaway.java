package com.github.jonteohr.tejbz.twitch.sql;

import com.github.jonteohr.tejbz.credentials.Credentials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Giveaway {

	/**
	 *
	 * @param name
	 * @param subbed
	 * @return
	 */
	public boolean addToGiveawayList(String name, boolean subbed) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO giveaway(name,subbed) VALUES(?,?);");
			pstmt.setString(1, name);
			pstmt.setInt(2, (subbed ? 1 : 0));
			pstmt.executeUpdate();

			con.close();
			pstmt.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 *
	 * @return
	 */
	public static boolean resetList() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("TRUNCATE TABLE giveaway;");
			pstmt.executeUpdate();

			con.close();
			pstmt.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
