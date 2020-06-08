package com.systex.chat.database;

import org.springframework.stereotype.Component;

@Component
public class Database {
	
	private String id;
	private String password;
	
	public String getID(){
		
		return id;
		
	}
	
	public void setID(String id) {
		
		this.id = id;
		
	}
	
	public String getPasswd() {
		
		return password;
		
	}
	
	public void setPasswd(String password) {
		
		this.password = password;
		
	}
	
}