package com.systex.chat.database;

import java.sql.Connection;

import org.springframework.stereotype.Component;

@Component
public class ConnectionObject {
	
	private Connection conn;
	
	public Connection getConn(){
		
		return conn;
		
	}
	
	public void setConn(Connection conn) {
		
		this.conn = conn;
		
	}
	
}
