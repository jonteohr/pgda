package com.github.condolent.tejbz.twitch.sql;

import com.github.condolent.tejbz.credentials.Credentials;

import java.sql.*;
import java.util.Date;

public class BankSQL {

	/**
	 *
	 * @param user
	 * @param coins
	 * @return
	 */
	public boolean incrementCoins(String user, int coins) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt;

			if(isUserInDatabase(user))
				pstmt = con.prepareStatement("UPDATE bank SET amount=amount+? WHERE user=?;");
			else
				pstmt = con.prepareStatement("INSERT INTO bank(amount,user) VALUES(?,?);");

			pstmt.setInt(1, coins);
			pstmt.setString(2, user);

			pstmt.executeUpdate();

			con.close();
			pstmt.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean collectDaily(String user, int coins) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt;
			if(!isUserInDatabase(user)) {
				pstmt = con.prepareStatement("INSERT INTO bank(user,amount,collected) VALUES(?,?,?);");
				pstmt.setString(1, user);
				pstmt.setInt(2, coins);
				pstmt.setTimestamp(3, new Timestamp(new Date().getTime()));
			} else {
				pstmt = con.prepareStatement("UPDATE bank SET amount=amount+?, collected=? WHERE user=?;");
				pstmt.setInt(1, coins);

				Timestamp ts = new Timestamp(new Date().getTime());
				pstmt.setTimestamp(2, ts);

				pstmt.setString(3, user);
			}


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
	 * @param user
	 * @param coins
	 * @return
	 */
	public boolean decrementCoins(String user, int coins) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt;
			pstmt = con.prepareStatement("UPDATE bank SET amount=amount-? WHERE user=?;");
			pstmt.setInt(1, coins);
			pstmt.setString(2, user);

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
	 * @param user
	 * @return
	 */
	public int getCoins(String user) {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("SELECT amount FROM bank WHERE user=?;");
			pstmt.setString(1, user);
			result = pstmt.executeQuery();

			int res = 0;

			while (result.next()) {
				res = result.getInt(1);
			}

			result.close();
			con.close();
			pstmt.close();

			return res;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public Date getLastCollected(String user) {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("SELECT collected FROM bank WHERE user=?;");
			pstmt.setString(1, user);
			result = pstmt.executeQuery();

			Date res = new Date();

			while (result.next()) {
				res = result.getTimestamp(1);
			}

			result.close();
			con.close();
			pstmt.close();

			return res;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isUserInDatabase(String user) {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(user) FROM bank WHERE user=?;");
			pstmt.setString(1, user);
			result = pstmt.executeQuery();

			int size = 0;

			while(result.next()) {
				size = result.getInt(1);
			}

			result.close();
			con.close();
			pstmt.close();

			return size > 0;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
