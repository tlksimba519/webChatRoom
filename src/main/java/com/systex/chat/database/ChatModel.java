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
	
	private final String accountTable = "chatmemberaccount";
	private final String historyTable = "history";
	
	//private final String validateUserCMD = "SELECT * FROM ? WHERE USERNAME = ?";
	//private final String signUpCMD = "INSERT INTO ?(USERNAME,PASSWORD) VALUES (?,?)";
	//private final String loginCMD = "SELECT * FROM ? WHERE USERNAME = ? AND PASSWORD = ?";
	//private final String saveTextCMD = "INSERT INTO ?(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,?,'')";
	//private final String saveFileCMD = "INSERT INTO ?(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,'',?)";
	//private final String loadHistoryCMD = "SELECT * FROM ? ORDER BY TIME DESC LIMIT ?,5";

	int historyMessageIndex = 0;
	/*
	 * 註冊功能
	 * 描述 : 獲取使用者帳號密碼，透過查詢指令確認是否為已註冊帳號，若不是則新增此帳戶資訊至資料庫
	 */
	public String signUp(Database d, Connection conn) throws SQLException {

		String status = "";
		String validateUserCMD = "SELECT * FROM " + accountTable + " WHERE USERNAME = ?";
		String signUpCMD = "INSERT INTO " + accountTable + "(USERNAME,PASSWORD) VALUES (?,?)";
		
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
		String loginCMD = "SELECT * FROM " + accountTable + " WHERE USERNAME = ? AND PASSWORD = ?";
		
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
			
			for(int i = 0;i < bytes.length;i++) {
				
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
				
			}
			
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
		String saveTextCMD = "INSERT INTO " + historyTable + "(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,?,'')";
		String saveFileCMD = "INSERT INTO " + historyTable + "(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,'',?)";
		
		if(type.equals("file")) {
			
			cmd = saveFileCMD;
			
		} else {
			
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
	 * 描述 : 使用查詢指令按時間排序後包成Map回傳給controller轉換成json。
	 */
	public Map<String, Object> loadMessage(Connection conn,int historyCount) throws SQLException {
		// 資料索引值
		int dataIndex = 1;
		// 資料總數
		int total = 0;
		// 每次存取資料量
		int dataPerTime = 5;
		
		String getCountCMD = "SELECT COUNT(*) FROM " + historyTable;
		String loadHistoryCMD = "SELECT * FROM " + historyTable + " ORDER BY TIME DESC LIMIT ?,?";
		
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String>temp = new ArrayList<String>();
		
		// 先下COUNT(*)指令獲得歷史紀錄總筆數
		PreparedStatement ps = conn.prepareStatement(getCountCMD);
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			
			total = rs.getInt(1);
		
		}
		
		ps = conn.prepareStatement(loadHistoryCMD);
		// 按照前端目前提取次數變更指令LIMIT範圍
		ps.setInt(1, historyCount*dataPerTime);
		ps.setInt(2, dataPerTime);
		rs = ps.executeQuery();
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
			
			result.put(Integer.toString(dataIndex), temp.toArray());
			
			dataIndex++;
			
		}
		
		// 若此次存取超出總筆數，在回傳map最後加上end供前端辨認
		if(historyCount * dataPerTime > total) {
			
			result.put(Integer.toString(dataIndex), "end");
			
		}
		
		return result;
		
	}
	
}