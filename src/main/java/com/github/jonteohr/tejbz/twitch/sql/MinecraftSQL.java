package com.github.jonteohr.tejbz.twitch.sql;

import com.github.jonteohr.tejbz.credentials.Credentials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MinecraftSQL {
	public static boolean connectToMinecraft(String user, String providedName) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://" + Credentials.DB_HOST.getValue() + ":3306/tejbz_mc?serverTimezone=UTC",
					Credentials.DB_USER.getValue(),
					Credentials.DB_PASS.getValue());

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO players(name,twitch) VALUES (?,?)");
			pstmt.setString(1, providedName);
			pstmt.setString(2, user);

			pstmt.executeUpdate();

			con.close();

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
