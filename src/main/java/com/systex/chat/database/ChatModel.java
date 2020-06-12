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
	
	loadMessageConfig config = new loadMessageConfig();
	/*
	 * 註冊功能
	 * 描述 : 獲取使用者帳號密碼，透過查詢指令確認是否為已註冊帳號，若不是則新增此帳戶資訊至資料庫
	 */
	public LoginStatus signUp(Database d, Connection conn) throws SQLException {

		String validateUserCMD = "SELECT * FROM " + accountTable + " WHERE USERNAME = ?";
		String signUpCMD = "INSERT INTO " + accountTable + "(USERNAME,PASSWORD) VALUES (?,?)";
		
		try {

			PreparedStatement ps = conn.prepareStatement(validateUserCMD);
			ps.setString(1, d.getID());
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				
				return LoginStatus.AlreadyUsed;
				
			} else {
				
				ps = conn.prepareStatement(signUpCMD);
				ps.setString(1, d.getID());
				ps.setString(2, hash(d.getPasswd()));
				ps.executeUpdate();
				return LoginStatus.SignupSuccess;
				
			}

		} catch (Exception e) {
			
			e.printStackTrace();
			return LoginStatus.Error;
			
		}
		
	}
	
	/*
	 * 登入功能
	 * 描述 : 獲取使用者帳號密碼，比對是否為會員。
	 */
	public LoginStatus login(Database d, Connection conn) throws SQLException {
		
		
		String loginCMD = "SELECT * FROM " + accountTable + " WHERE USERNAME = ? AND PASSWORD = ?";
		
		try {
			
			PreparedStatement ps = conn.prepareStatement(loginCMD);
			ps.setString(1, d.getID());
			ps.setString(2, hash(d.getPasswd()));
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				
				return LoginStatus.LoginSuccess;
				
			} else {
				
				return LoginStatus.Incorrect;
				
			}
			
		} catch (Exception e) {

			e.printStackTrace();
			return LoginStatus.Error;
			
		}
		
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
		
		// 每次存取資料量
		int dataPerTime = 5;
		
		String getCountCMD = "SELECT COUNT(*) FROM " + historyTable;
		String getRangeCMD = "SELECT * FROM " + historyTable + " ORDER BY TIME DESC";
		String loadHistoryCMD = "SELECT * FROM " + historyTable + " WHERE TIME<=? ORDER BY TIME DESC LIMIT ?,?";
		
		Map<String, Object> result = new HashMap<String, Object>();
		ArrayList<String>temp = new ArrayList<String>();
		PreparedStatement ps;
		ResultSet rs;
		
		// 第一次呼叫保存參數
		if(historyCount==0) {
			
			// COUNT(*)指令獲得歷史紀錄總筆數
			ps = conn.prepareStatement(getCountCMD);
			rs = ps.executeQuery();

			if (rs.next()) {

				config.setTotal(rs.getInt(1));

			}
			// 取得當下最新訊息做基準
			ps = conn.prepareStatement(getRangeCMD);
			rs = ps.executeQuery();
			
			if (rs.next()) {

				config.setLastestMessage(rs.getString(1));

			}
		
		}

		ps = conn.prepareStatement(loadHistoryCMD);
		// 按照前端目前提取次數變更指令LIMIT範圍
		ps.setString(1, config.getLastestMessage());
		ps.setInt(2, historyCount*dataPerTime);
		ps.setInt(3, dataPerTime);
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
		if(historyCount * dataPerTime > config.getTotal()) {
			
			result.put(Integer.toString(dataIndex), "end");
			
		}
		
		return result;
		
	}
	
	/*
	 * 讀取訊息參數class
	 */
	public class loadMessageConfig{
		
		private int total;
		private String lastestMessage;
		
		public int getTotal() {
			
			return total;
			
		}
		public void setTotal(int total) {
			
			this.total = total;
			
		}
		public String getLastestMessage() {
			
			return lastestMessage;
			
		}
		public void setLastestMessage(String lastestMessage) {
			
			this.lastestMessage = lastestMessage;
			
		}
		
	}
	
}