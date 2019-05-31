package com.doctordark.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

public class DatabaseTableAPI {
	
	private DatabaseAPI database;
	private String row1name, row2name, tablename;

	public DatabaseTableAPI(final DatabaseAPI database, final String tableName, final String firstRowName, final String secondRowName) {
		
		try {
			this.database = database;
			row1name = firstRowName;
			row2name = secondRowName;
			tablename = tableName;
			Statement statement = database.getConnection().createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + "(" + firstRowName + " varchar(255), " + secondRowName + " varchar(255), PRIMARY KEY(" + firstRowName + "))");
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().severe("Unable to create table " + tableName + ".");
		}
		
	}
	
	public Boolean replaceIntoTable(String value1, String value2) {
		try {
			Statement statement = database.getConnection().createStatement();
			statement.executeUpdate("REPLACE INTO " + tablename + " (" + row1name + ", " + row2name + ") VALUES ('" + value1 + "', '" + value2 + "')");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public Boolean insertIntoTable(String value1, String value2) {
		try {
			Statement statement = database.getConnection().createStatement();
			statement.executeUpdate("INSERT INTO " + tablename + " (" + row1name + ", " + row2name + ") VALUES ('" + value1 + "', '" + value2 + "')");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public Boolean deleteFromTable(String value1) {
		try {
			Statement statement = database.getConnection().createStatement();
			String query = "DELETE FROM " + tablename + " WHERE " + row1name + " = '" + value1 + "'";
			statement.executeUpdate(query);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public String getFromTable(String value1) {
		try {
			String query = "SELECT * FROM " + tablename + " WHERE " + row1name + "='" + value1 + "'";
			
			Connection con = database.getConnection();
			PreparedStatement statement = con.prepareStatement(query);
			
			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				return result.getString(row2name);
			}
		} catch (Exception e) {
			return null;
		}
		
		return null;
	}
	
}
