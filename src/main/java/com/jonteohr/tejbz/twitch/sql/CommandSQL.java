package com.jonteohr.tejbz.twitch.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jonteohr.tejbz.credentials.Credentials;

public class CommandSQL {

	/**
	 * 
	 * @param cmdName
	 * @param reply
	 * @return
	 */
	public boolean addCommand(String cmdName, String reply) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
						Credentials.DB_USER.getValue(),
						Credentials.DB_PASS.getValue());

			reply = reply.replace("'", "\'");

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO commands(cmd,reply) VALUES(?,?);");
			pstmt.setString(1, cmdName);
			pstmt.setString(2, reply);
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
	 * @param cmdName
	 * @return
	 */
	public boolean deleteCommand(String cmdName) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("DELETE FROM commands WHERE cmd=?;");
			pstmt.setString(1, cmdName);
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
	 * @param cmdName
	 * @param reply
	 * @return
	 */
	public boolean editCommand(String cmdName, String reply) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("UPDATE commands SET reply=? WHERE cmd=?;");
			pstmt.setString(1, reply);
			pstmt.setString(2, cmdName);

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
	 * @param cmdName
	 * @return
	 */
	public static boolean incrementUses(String cmdName) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("UPDATE commands SET uses=uses+1 WHERE cmd=?;");
			pstmt.setString(1, cmdName);

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
	 * @param cmdName
	 * @return
	 */
	public static int getUses(String cmdName) {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("SELECT uses FROM commands WHERE cmd=?;");
			pstmt.setString(1, cmdName);
			
			result = pstmt.executeQuery();

			int res = 0;

			while(result.next()) {
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

	/**
	 * 
	 * @return
	 */
	public List<String> getCommands() {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			Statement pstmt = con.createStatement();
			result = pstmt.executeQuery("SELECT cmd FROM commands;");

			List<String> cmds = new ArrayList<String>();

			while (result.next()) {
				cmds.add(result.getString(1));
			}
			
			result.close();
			con.close();

			return cmds;

		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}

	/**
	 * 
	 * @param cmdName
	 * @return
	 */
	public String getCommandReply(String cmdName) {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("SELECT reply FROM commands WHERE cmd=?;");
			pstmt.setString(1, cmdName);
			result = pstmt.executeQuery();

			String reply = null;

			while(result.next()) {
				reply = result.getString(1);
			}
			
			result.close();
			con.close();

			return reply;

		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public Map<String, String> getCommandsMap() {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("SELECT * FROM commands;");
			result = pstmt.executeQuery();

			Map<String, String> response = new HashMap<String, String>();

			while(result.next()) {
				response.put(result.getString(1), result.getString(2));
			}
			
			result.close();
			con.close();

			return response;

		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	public Map<String, String> getSpecialCommands() {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("SELECT cmd, type FROM commands WHERE type <> NULL;");
			result = pstmt.executeQuery();

			Map<String, String> response = new HashMap<String, String>();

			while(result.next()) {
				response.put(result.getString(1), result.getString(2));
			}
			
			result.close();
			con.close();

			return response;

		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
