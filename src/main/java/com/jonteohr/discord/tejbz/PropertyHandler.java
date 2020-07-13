package com.jonteohr.discord.tejbz;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyHandler {
	/**
	 * Retrieves the value of a property key in bot.properties
	 * @param key {@link java.lang.String String} key
	 * @return a {@link java.lang.String String} value
	 * @see #setProperty(String, String)
	 */
	public String getPropertyValue(String key) {
		try(InputStream inputStream = new FileInputStream("bot.properties")) {
			Properties prop = new Properties();
			
			prop.load(inputStream);
			
			return prop.getProperty(key);
		} catch(IOException e) {
			System.out.println(e);
			return null;
		}
	}
	
	/**
	 * Sets the value of a key, or creates one.
	 * @param key a {@link java.lang.String String} key
	 * @param value a {@link java.lang.String String} value
	 * @return {@code true} if success
	 * @see #getPropertyValue(String)
	 */
	public boolean setProperty(String key, String value) {
		try(InputStream inputStream = new FileInputStream("bot.properties")) {
			
			Properties prop = new Properties();
			
			prop.load(inputStream);
			
			try(OutputStream outputStream = new FileOutputStream("bot.properties")) {
				prop.setProperty(key, value);
				prop.store(outputStream, null);
				
				return true;
			} catch (IOException e) {
				System.out.println(e);
			}
			
			return true;
			
		} catch(IOException e) {
			System.out.println(e);
			return false;
		}
	}
}
