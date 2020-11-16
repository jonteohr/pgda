package com.github.condolent.tejbz.twitch.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.github.condolent.tejbz.credentials.Credentials;

public class SettingsSQL {
	public int getSettingValue(String setting) {
		ResultSet result;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/" + Credentials.DB_NAME.getValue() + "?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());
			
			PreparedStatement pstmt = con.prepareStatement("SELECT status FROM settings WHERE setting=?;");
			pstmt.setString(1, setting);
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
}
