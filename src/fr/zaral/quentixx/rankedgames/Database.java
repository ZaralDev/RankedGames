package fr.zaral.quentixx.rankedgames;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	protected static PreparedStatement createStatement(String query) {
		if (!isConnected())
			Database.connect();
		try {
			return Database.connection.prepareStatement(query);
		} catch (SQLException ex) {
			Bukkit.getServer().getLogger().warning("An SQL error occured: " + ex.getMessage());
		}
		return null;
	}
	
	public static boolean isRegistredUser(String uuid) throws SQLException {
		PreparedStatement queryStatement = createStatement("SELECT uuid FROM users WHERE uuid = ?");
		queryStatement.setString(1, uuid);
		queryStatement.executeQuery();
		ResultSet resultSet = queryStatement.getResultSet();
		boolean registred = resultSet.next();
		return registred;
	}
	
	public static void registerUser(String name, String uuid) throws SQLException {
		PreparedStatement queryStatement = createStatement("INSERT INTO users (uuid, name, ratio) VALUES (?, ?, 0)");
		queryStatement.setString(1, uuid);
		queryStatement.setString(2, name);
		queryStatement.executeUpdate();
		queryStatement.close();
	}
	
	public static int getRatio(String name) throws SQLException {
		PreparedStatement queryStatement = createStatement("SELECT ratio FROM users WHERE name = ?");
		queryStatement.setString(1, name);
		queryStatement.executeQuery();
		ResultSet resultSet = queryStatement.getResultSet();
		resultSet.next();
		int ratio = resultSet.getInt(1);
		queryStatement.close();
		return ratio;
	}
	
	public static int updateRatio(String name, int ratio) throws SQLException {
		PreparedStatement queryStatement = createStatement("UPDATE users SET ratio = ? WHERE name = ?");
		queryStatement.setInt(1, ratio);
		queryStatement.setString(2, name);
		queryStatement.executeUpdate();
		queryStatement.close();
		return ratio;
	}
	
	public static void updateName(String name, String uuid) throws SQLException {
		PreparedStatement queryStatement = createStatement("UPDATE users SET name = ? WHERE uuid = ?");
		queryStatement.setString(1, name);
		queryStatement.setString(2, uuid);
		queryStatement.executeUpdate();
		queryStatement.close();
	}
}
