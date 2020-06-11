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
public class ChatModel {
	
	private final String validateUserCMD = "SELECT * FROM chatmemberaccount WHERE USERNAME = ?";
	private final String signUpCMD = "INSERT INTO chatmemberaccount(USERNAME,PASSWORD) VALUES (?,?)";
	private final String loginCMD = "SELECT * FROM chatmemberaccount WHERE USERNAME = ? AND PASSWORD = ?";
	private final String saveTextCMD = "INSERT INTO history(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,?,'')";
	private final String saveFileCMD = "INSERT INTO history(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,'',?)";
	private final String loadHistoryCMD = "SELECT * FROM history ORDER BY TIME ASC";
	
	/*
	 * 註冊功能
	 * 描述 : 獲取使用者帳號密碼，透過查詢指令確認是否為已註冊帳號，若不是則新增此帳戶資訊至資料庫
	 */
	public String signUp(Database d, Connection conn) throws SQLException {

		String status = "";
		
		try {

			PreparedStatement ps = conn.prepareStatement(validateUserCMD);
			ps.setString(1, d.getID());
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				
				status = "used";
				
			} else {
				
				ps = conn.prepareStatement(signUpCMD);
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
	public String login(Database d, Connection conn) throws SQLException {
		
		String status = "";
		
		try {
			
			PreparedStatement ps = conn.prepareStatement(loginCMD);
			ps.setString(1, d.getID());
			ps.setString(2, hash(d.getPasswd()));
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
		char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(Unencrypt.getBytes("UTF-8"));
			byte[] bytes = md.digest();
			char buffer[] = new char[bytes.length*2];
			int k = 0;
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0;i < bytes.length;i++) {
				
				buffer[k++] = hex[bytes[i] >>> 4 & 0xf];
				buffer[k++] = hex[bytes[i] & 0xf];
				
			}
			
			sb.append(buffer);
			encrypted = sb.toString();
		
		} catch(Exception e) {
			
			e.printStackTrace();
			
		}
		
		return encrypted;
		
	}
	
	/*
	 * 儲存訊息
	 * 描述 : 透過獲得訊息類型判斷來切換不同指令儲存訊息
	 */
	public void storeMessage(String user, String content, String type, Connection conn) throws SQLException {
		
		String cmd = null;
		
		if(type.equals("file")) {
			
			cmd = saveFileCMD;
			
		}
		else {
			
			cmd = saveTextCMD;
			
		}
		
		PreparedStatement ps = conn.prepareStatement(cmd);
		ps.setString(1, LocalDateTime.now().toString());
		ps.setString(2, user);
		ps.setString(3, content);
		ps.executeUpdate();
		
	}
	
	/*
	 * 讀取訊息
	 * 描述 : 使用查詢指令按時間排序後包裝成Map回傳給controller轉換成json。
	 */
	public Map<String, Object> loadMessage(Connection conn) throws SQLException {
		
		int index = 1;
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String>temp = new ArrayList<String>();
		
		PreparedStatement ps = conn.prepareStatement(loadHistoryCMD);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		
		while(rs.next()) {
			
			temp.clear();
			
			for(int i = 1;i <= columnsNumber;i++) {
				
				if(rs.getString(i).equals("")) {
					
				} else {
					
					temp.add(rs.getString(i));
					
				}
				
			}
			
			result.put(Integer.toString(index), temp.toArray());
			
			index++;
			
		}
		
		return result;
		
	}
	
}