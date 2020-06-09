package com.systex.chat.database;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/*
 * 資料庫model
 */
@Component
public class DatabaseModel {

	private  final String accountTable = "chatmemberaccount";
	private  final String saveText = "INSERT INTO history(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,?,'')";
	private  final String saveFile = "INSERT INTO history(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,'',?)";
	private  final String loadMSG = "SELECT * FROM history ORDER BY TIME ASC";
	
	/*
	 * 註冊功能
	 * 描述 : 獲取使用者帳號密碼，透過查詢指令確認是否為已註冊帳號，若不是則新增此帳戶資訊至資料庫
	 */
	public String signup(Database d,Connection conn) throws SQLException {

		String status = "";
		
		try {
			
			String checkSQL = "SELECT * FROM " + accountTable + " WHERE USERNAME = '"
					+ d.getID()+ "'";
			String signupSQL = "INSERT INTO " + accountTable + "(USERNAME,PASSWORD) VALUES (?,?)";
			
			PreparedStatement ps = conn.prepareStatement(checkSQL);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				
				status = "used";
				
			} else {
				
				ps = conn.prepareStatement(signupSQL);
				ps.setString(1, d.getID());
				ps.setString(2, hash(d.getPasswd()));
				ps.executeUpdate();
				status = "success";
				
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			status = "fail";
			
		}

		return status;
		
	}
	
	/*
	 * 登入功能
	 * 描述 : 獲取使用者帳號密碼，比對是否為會員。
	 */
	public String login(Database d,Connection conn) throws SQLException {
		
		String status = "";
		
		try {
			
			String loginSQL = "SELECT * FROM " + accountTable+ " WHERE USERNAME = '" + d.getID()
				+ "' AND PASSWORD = '"+hash(d.getPasswd())+"'";
			PreparedStatement ps = conn.prepareStatement(loginSQL);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				
				status = "success";
				
			} else {
				
				status = "wrong";
				
			}
			
		} catch (Exception e) {

			e.printStackTrace();
			status = "fail";
			
		}
		
		return status;
		
	}
	
	/*
	 * 登出功能
	 * 描述 : 註冊功能為暫時連線需求，註冊完畢即先釋放connection物件
	 */
	public void logout(Connection conn) throws SQLException {

		conn.close();

	}
	
	/*
	 * MD5加密
	 * 描述 : 對密碼進行MD5加密，避免直接使用明文存至資料庫
	 */
	public String hash(String Unencrypt) {
		String encrypted = null;
		try {
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(Unencrypt.getBytes());
			byte[] bytes = md.digest();
			
			StringBuilder sb = new StringBuilder();
			
			for(int i=0;i<bytes.length;i++) {
				
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
				
			}
			
			encrypted = sb.toString();
		
		}catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
		return encrypted;
		
	}
	
	/*
	 * 儲存訊息
	 * 描述 : 透過獲得訊息類型判斷來切換不同指令儲存訊息
	 */
	public void messageStorage(String user,String content,String type,Connection conn) throws SQLException {
		
		String cmd = null;
		
		if(type.equals("file")) {
			
			cmd = saveFile;
			
		}
		else {
			
			cmd = saveText;
			
		}
		
		PreparedStatement ps = conn.prepareStatement(cmd);
		ps.setString(1,LocalDateTime.now().toString());
		ps.setString(2,user);
		ps.setString(3,content);
		ps.executeUpdate();
		
	}
	
	/*
	 * 讀取訊息
	 * 描述 : 使用查詢指令按時間排序後包裝成Map回傳給controller轉換成json。
	 */
	public Map<String, Object> messageLoad(Connection conn) throws SQLException {
		
		int index = 1;
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String>temp = new ArrayList<String>();
		
		PreparedStatement ps = conn.prepareStatement(loadMSG);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		
		while(rs.next()) {
			
			temp.clear();
			
			for(int i=1;i<=columnsNumber;i++) {
				
				if(rs.getString(i).equals("")) {
					
				}else {
					
					temp.add(rs.getString(i));
					
				}
				
			}
			
			result.put(Integer.toString(index),temp.toArray());
			
			index++;
			
		}
		
		return result;
		
	}
	
}