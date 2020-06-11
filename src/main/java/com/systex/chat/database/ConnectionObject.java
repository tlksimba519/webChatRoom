package com.systex.chat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Component;

/*
 * database connection物件class，用於轉換為bean方便spring管理。
 */
@Component
public class ConnectionObject {
	
	// db資訊
	private  final String dbInfo = "jdbc:mysql://localhost:3306/mysql?serverTimezone=UTC";
	private  final String dbUser = "root";
	private  final String dbPassword = "Leo0826519";
	
	private Connection conn;
	
	public void init() throws SQLException {
		
        conn = DriverManager.getConnection(dbInfo, dbUser, dbPassword);
        
    }
	
	public void destroy() throws SQLException {
		
        conn.close();
        
    }
	
	public Connection getConn(){
		
		return conn;
		
	}
	
	public void setConn(Connection conn) {
		
		this.conn = conn;
		
	}
	
}
