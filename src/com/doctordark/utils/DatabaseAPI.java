package com.doctordark.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import me.rainny.reaper.HCF;

public class DatabaseAPI {
	
	private static Connection connection;
	private String host, database, username, password;
	private int port;
	public HCF main;
	
	public DatabaseAPI(final HCF main, final String ip, final int port, final String username, final String database, final String password) {
		this.host = ip;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		try {
			long before = System.currentTimeMillis();
			openConnection();
			long after = System.currentTimeMillis();
			long calc = after - before;
			System.out.println("Succesfully connected to the database in " + calc + "ms");
			
			
		} catch (Exception e) {
			System.out.println("Unable to connect to the database.");
			System.out.println("Unable to connect to the database.");
			System.out.println("Unable to connect to the database.");
			e.printStackTrace();
		}
		
		
	}
	
	private void openConnection() throws SQLException, ClassNotFoundException {
		if(connection != null && !connection.isClosed()) {
			return;
		}
		
		synchronized (this) {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host+ ":" + port + "/" + database, username, password);
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void execQuery(String query) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
