package com.jonteohr.discord.tejbz;

public enum LogType {
	CRITICAL(0xD52D42),
	INFORMATION(0x398dad),
	WARNING(0xF5C219);
	
	private final int color;
	
	LogType(int color) {
		this.color = color;
	}
	
	/**
	 * Retrieves the color of the LogType
	 * @return
	 */
	public int getColor() {
		return color;
	}
	
	/**
	 * Get color from specified LogType
	 * @param logtype
	 * @return
	 */
	public static int getColorFromType(LogType logtype) {
		return logtype.getColor();
	}
}
