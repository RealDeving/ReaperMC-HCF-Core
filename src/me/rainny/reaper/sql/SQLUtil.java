package me.rainny.reaper.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import me.rainny.reaper.HCF;
import me.rainny.reaper.ymls.SettingsYML;

public class SQLUtil {

	public Connection c;
	public String host, database, username, password, table;
	public int port;
	public List<String> tables;

	public void setup() {
		this.host = SettingsYML.SQL_HOST;
		this.port = SettingsYML.SQL_PORT;
		this.database = SettingsYML.SQL_DATABASE;
		this.username = SettingsYML.SQL_USERNAME;
		this.password = SettingsYML.SQL_PASSWORD;
		tables = new ArrayList<String>();
		open();
	}

	public void open() {
		try {
			synchronized (this) {
				if (c != null && !c.isClosed()) {
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				c = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username,
						password);
				Bukkit.getConsoleSender().sendMessage(
						new String[] { "MySQL connected!", "Congrats! It works!", "Now go and do more coding!" });
				HCF.sqlWorks = true;
			}
		} catch (Exception e) {
			HCF.sqlWorks = false;
		}
	}

	public void createTable(String table, List<String> types, List<String> values) {
		try {
			PreparedStatement s = c.prepareStatement(
					"CREATE TABLE IF NOT EXISTS " + table + "(" + getTypesAndValues(types, values) + ")");
			s.executeUpdate();
		} catch (Exception e) {
		}
	}

	public boolean valueExistsInKey(String table, String keyName, String value) {
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM " + table + " WHERE " + keyName + "=?");
			s.setString(1, value);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public void createValuesInKeys(String table, List<String> keys, List<String> values) {
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM " + table + " WHERE " + keys.get(0) + "=?");
			s.setString(1, values.get(0));
			ResultSet rs = s.executeQuery();
			rs.next();
			if (!valueExistsInKey(table, keys.get(0), values.get(0))) {
				PreparedStatement i = c.prepareStatement(
						"INSERT INTO " + table + "(" + getKeys(keys) + ") VALUES (" + getValues(values) + ")");
				i.executeUpdate();
			}
		} catch (Exception e) {
		}
	}

	public void updateValueInKey(String table, String keyName, String valueName, String key, String value) {
		try {
			PreparedStatement s = c
					.prepareStatement("UPDATE " + table + " SET " + valueName + "=? WHERE " + keyName + "=?");
			s.setString(1, value);
			s.setString(2, key);
			s.executeUpdate();
		} catch (Exception e) {
		}
	}

	public String getValue(String table, String valueName) {
		try {
			PreparedStatement s = c.prepareStatement("SELECT " + valueName + " FROM " + table);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				return rs.getString(valueName);
			}
		} catch (Exception e) {
		}
		return "";
	}

	public String getValue(String table, String key, String keyName, String valueName) {
		try {
			PreparedStatement s = c
					.prepareStatement("SELECT " + valueName + " FROM " + table + " WHERE " + keyName + "=?");
			s.setString(1, key);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				return rs.getString(valueName);
			}
		} catch (Exception e) {
		}
		return "";
	}

	public ResultSet getValues(String table) {
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM " + table);
			ResultSet rs = s.executeQuery();
			return rs;
		} catch (Exception e) {
		}
		return null;
	}

	public ResultSet getValues(String table, String keyName, String key) {
		try {
			PreparedStatement s = c.prepareStatement("SELECT * FROM " + table + " WHERE " + keyName + "=?");
			s.setString(1, key);
			ResultSet rs = s.executeQuery();
			return rs;
		} catch (Exception e) {
		}
		return null;
	}

	private String getTypesAndValues(List<String> types, List<String> values) {
		if (types.size() != values.size()) {
			Bukkit.getLogger().severe("Types and Values are not the same size!");
		}
		String str = "";
		for (int i = 0; i < types.size(); i++) {
			str += values.get(i) + " " + types.get(i) + ", ";
		}
		str += "PRIMARY KEY(" + values.get(0) + ")";
		return str;
	}

	private String getKeys(List<String> keys) {
		String str = "`";
		for (String key : keys) {
			str += key + "`,`";
		}
		str = str.substring(0, str.length() - 2);
		return str;
	}

	private String getValues(List<String> keys) {
		String str = "'";
		for (String key : keys) {
			str += key + "','";
		}
		str = str.substring(0, str.length() - 2);
		return str;
	}

}
