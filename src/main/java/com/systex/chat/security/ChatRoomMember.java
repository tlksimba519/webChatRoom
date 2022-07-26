package com.systex.chat.security;

public class ChatRoomMember {
	private String userid;
	private String role;
	private String password;

	public ChatRoomMember() {

	}

	public ChatRoomMember(String userId, String role, String passWord) {
		this.userid = userId;
		this.role = role;
		this.password = passWord;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
