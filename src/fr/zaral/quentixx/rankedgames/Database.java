package fr.zaral.quentixx.rankedgames;

public class Database {
	
	private static String url, user, pass;
	
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
		
	}
	
	public static boolean isConnected() {
		return false;
	}
	
	public static void closeConnection() {
		
	}
}
