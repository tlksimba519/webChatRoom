package com.systex.chat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class DatabaseModel {
	
	
	private static final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String tableName = "user";
	private static final String dbInfo = "jdbc:sqlserver://10.10.56.198:1433;"+
			"databaseName=exercise_db;user=sa;password=systex.6214";
	public static Connection conn;

	
	public static Connection getConnetion() throws SQLException {
		
		try {
			
			Class.forName(driver);
			conn = DriverManager.getConnection(dbInfo);
			
		} catch (Exception e) {
			
			System.out.println("找不到驅動程式類別");
			e.printStackTrace();
			conn.close();
			System.exit(0);
			
		}
		
		return conn;
		
	}
	
	
	public static boolean login(Database d,Connection conn) throws SQLException {
		
		boolean status = false;
		
		try {
			
			String sql = "SELECT * FROM " + tableName+ " WHERE UID = '"+d.getID()+"' AND ACCOUNT = '"+d.getPasswd()+"'";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) status = true;
			
		} catch (Exception e) {

			e.printStackTrace();
			conn.close();
			System.exit(0);
			
		}
		
		return status;
		
	}
	
}