package com.github.jonteohr.tejbz.twitch.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.github.jonteohr.tejbz.credentials.Credentials;

public class AutoMessageSQL {
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean addAutoMessage(String message) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
						Credentials.DB_USER.getValue(),
						Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO messages(message) VALUES(?);");
			pstmt.setString(1, message);
			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	public boolean removeAutoMessage(String message) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
						Credentials.DB_USER.getValue(),
						Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("DELETE FROM messages WHERE message=?;");
			pstmt.setString(1, message);
			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> getMessages() {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			Statement pstmt = con.createStatement();
			result = pstmt.executeQuery("SELECT message FROM messages ORDER BY item_order ASC;");

			List<String> msgs = new ArrayList<>();

			while (result.next()) {
				msgs.add(result.getString(1));
			}
			
			result.close();
			con.close();

			return msgs;

		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public int getInterval() {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			Statement pstmt = con.createStatement();
			result = pstmt.executeQuery("SELECT status FROM settings WHERE setting='messageInterval';");

			int res = 0;

			while (result.next()) {
				res = result.getInt(1);
			}
			
			result.close();
			con.close();

			return res;

		} catch (Exception e) {
			System.out.println(e);
			return -1;
		}
	}
	
	public boolean setInterval(int interval) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
						Credentials.DB_USER.getValue(),
						Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("UPDATE settings SET status=" + interval + " WHERE setting='messageInterval';");
			pstmt.setInt(1, interval);
			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
