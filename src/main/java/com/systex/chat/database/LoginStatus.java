package com.systex.chat.database;

public enum LoginStatus {
	
	LoginSuccess("/main.html"),
	SignupSuccess("/index.html"),
	AlreadyUsed("/alreadyUsed.html"),
	Incorrect("/loginFail.html"),
	Error("/error.html");
	
	private String url;
	
	LoginStatus(String url) {
		
		this.url = url;
		
	}

	public String getUrl() {
		
		return url;
		
	}

}