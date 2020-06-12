package com.systex.chat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
 * database connection物件class，用於轉換為bean方便spring管理。
 */
@Component
public class ConnectionObject {
	
	@Value("${databaseInfo}")
	private String Info;
	@Value("${databaseUser}")
	private String User;
	@Value("${databasePassword}")
	private String Password;
	
	private Connection conn;
	
	public void init() throws SQLException {
		
        conn = DriverManager.getConnection(Info, User, Password);
        
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
