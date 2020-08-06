package com.jonteohr.discord.tejbz.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.jonteohr.discord.tejbz.credentials.Credentials;

public class WatchTimeSQL {
	
	/**
	 * Increments the watch time for the specified user by 1.
	 * @param viewer
	 * @param time
	 * @return {@code true} if successful
	 * @see #addToWatchTime(String)
	 */
	public boolean setWatchTime(String viewer, int time) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
						Credentials.DB_USER.getValue(),
						Credentials.DB_PASS.getValue());
			
			int savedTime = getWatchTime(viewer);
			int diffTime = time - savedTime;

			PreparedStatement pstmt = con.prepareStatement("UPDATE watchtime SET time=? WHERE viewer=?");
			pstmt.setInt(1, diffTime);
			pstmt.setString(2, viewer);
			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Adds the specified viewer to the database table with 1 minutes of watchtime.
	 * @param viewer
	 * @param time
	 * @return {@code true} if successful
	 * @see #incrementWatchTime(String)
	 */
	public boolean addToWatchTime(String viewer, int time) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
						Credentials.DB_USER.getValue(),
						Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO watchtime(viewer,time) VALUES(?, ?)");
			pstmt.setString(1, viewer);
			pstmt.setInt(2, time);
			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Retrieves the total watchtime of a viewer in minutes.
	 * @param viewer
	 * @return minutes in an {@link java.lang.Integer Integer}
	 */
	public int getWatchTime(String viewer) {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("SELECT time FROM watchtime WHERE viewer=?;");
			pstmt.setString(1, viewer);
			
			result = pstmt.executeQuery();

			int res = 0;

			while(result.next()) {
				res = result.getInt(1);
			}
			
			con.close();

			return res;

		} catch (Exception e) {
			System.out.println(e);
			return -1;
		}
	}
	
	/**
	 * Gets all saved watchtimes as a map.
	 * @return a {@link java.util.Map Map} with every viewer and their watchtime.
	 */
	public Map<String, Integer> getWatchTimeList() {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("SELECT * FROM watchtime;");
			result = pstmt.executeQuery();

			Map<String, Integer> res = new HashMap<String, Integer>();

			while(result.next()) {
				res.put(result.getString(1), result.getInt(2));
			}
			
			con.close();

			return res;

		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
