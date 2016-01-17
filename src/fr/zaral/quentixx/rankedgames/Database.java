package fr.zaral.quentixx.rankedgames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class Database {
	
	/**
	 * Created by Quentixx at 17/01/2016
	 */
	
	private static String url, user, pass;
	private static Connection connection;
	
	public static String getUrl() {
		return url;
	}
	
	public static String getUser() {
		return user;
	}
	
	public static String getPass() {
		return pass;
	}
	
	public static String[] init(String dbUrl, String dbUser, String dbPass) {
		url = dbUrl;
		user = dbUser;
		pass = dbPass;
		return new String[]{dbUrl, dbUser, dbPass};
	}
	
	public static void connect() {
		try {
			connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static Connection getConnection() {
		return connection;
	}
	
	public static boolean isConnected() {
		if (connection != null) {
			try {
			if (!connection.isClosed())
				return true;
			} catch (SQLException ex) {
				Bukkit.getServer().getLogger().log(Level.WARNING, "Can not resolve database connection");
			}
		}
		return false;
	}
	
	public static void closeConnection() {
		try {
			connection.close();
			Bukkit.getServer().getLogger().log(Level.INFO, "Database closed!");
		} catch (SQLException ex) {
			Bukkit.getServer().getLogger().log(Level.SEVERE, "Can not close database: " + ex.getMessage());
		}
	}
}
