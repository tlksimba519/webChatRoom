package com.systex.chat.database;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DatabaseModel {
	
	private static final String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String accountTable = "ChatMemberAccount";
	private static final String dbInfo = "jdbc:sqlserver://10.10.56.198:1433;"+
			"databaseName=exercise_db;user=sa;password=systex.6214";
	private static final String saveText = "INSERT INTO history(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,?,'')";
	private static final String saveFile = "INSERT INTO history(TIME,USERNAME,TEXT,FILEPATH) VALUES (?,?,'',?)";
	private static final String loadMSG = "SELECT * FROM history ORDER BY TIME ASC";
	public static Connection conn;

	/*
	 * 獲得Connection物件
	 */
	public static Connection getConnetion() throws SQLException {
		
		try {
			
			Class.forName(driver);
			conn = DriverManager.getConnection(dbInfo);
			
		} catch (Exception e) {
			
			System.out.println("找不到驅動程式類別");
			e.printStackTrace();
			conn.close();
			System.exit(0);
			
		}
		
		return conn;
		
	}
	
	/*
	 * 註冊
	 */
	public static boolean signup(Database d,Connection conn) throws SQLException {

		boolean status = false;
		
		try {
			
			String sql = "INSERT INTO " + accountTable + "(USERNAME,PASSWORD) VALUES (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, d.getID());
			ps.setString(2, hash(d.getPasswd()));
			ps.executeUpdate();
			status = true;

		} catch (Exception e) {
			
			e.printStackTrace();
			conn.close();
			System.exit(0);
			
		}

		return status;
		
	}
	
	/*
	 * 登入
	 */
	public static boolean login(Database d,Connection conn) throws SQLException {
		
		boolean status = false;
		
		try {
			
			String sql = "SELECT * FROM " + accountTable+ " WHERE USERNAME = '"+d.getID()+"' AND PASSWORD = '"+hash(d.getPasswd())+"'";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) status = true;
			
		} catch (Exception e) {

			e.printStackTrace();
			conn.close();
			System.exit(0);
			
		}
		
		return status;
		
	}
	
	/*
	 * 登出
	 */
	public static void logout(Connection conn) throws SQLException {

		conn.close();

	}
	
	/*
	 * MD5加密
	 */
	public static String hash(String Unencrypt) {
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
	 * 儲存歷史訊息
	 */
	public static void storage(String user,String content,String type) throws SQLException {
		
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
	 * 讀取歷史訊息
	 */
	public static Map<String, Object> load() throws SQLException {
		
		int index = 1;
		Map<String, Object> result = new HashMap();
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