package com.systex.chat.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.systex.chat.database.ConnectionObject;
import com.systex.chat.database.Database;
import com.systex.chat.database.DatabaseModel;
import com.systex.chat.file.file;
import com.systex.chat.message.ChatMessage;

@Controller
public class MainController {
	
	@Autowired
	DatabaseModel databaseModel;
	
	@Autowired
	Database databaseBean;
	
	@Autowired
	file f;
	
	@Autowired
	ConnectionObject conn;
	
	/*
	 * 註冊功能
	 * 描述 : 從註冊頁獲取使用者帳號及密碼，比對是否為未註冊帳號，註冊完畢即跳轉回登入頁。
	 */
	@PostMapping("/signup")
	public void signup(@RequestParam String UserName, @RequestParam String Password, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {

		String status;
		
		databaseBean.setID(UserName);
		databaseBean.setPasswd(Password);
		
		status = databaseModel.signup(databaseBean, conn.getConn());
		
		if(status.equals("success")) {
			
			response.sendRedirect("/index.html");
			
		} else if(status.equals("used")) {
				
			response.sendRedirect("/alreadyUsed.html");
				
		} else {
			
			response.sendRedirect("/error.html");
			
		}

	}
	
	/*
	 * 登入功能
	 * 描述 : 從登入頁獲取使用者帳號及密碼，比對資料庫中是否有對應資料，登入成功後跳轉至聊天室主頁。
	 */
	@PostMapping("/login")
	public void login(@RequestParam String UserName, @RequestParam String Password, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {

		String status;
		
		databaseBean.setID(UserName);
		databaseBean.setPasswd(Password);
		
		status = databaseModel.login(databaseBean, conn.getConn());
		
		if(status.equals("success")) {
			
			response.sendRedirect("/main.html");
			
		}
		
		else if(status.equals("wrong")) {
				
			response.sendRedirect("/loginFail.html");
				
		} else {
			
			response.sendRedirect("/error.html");
			
		}

	}
	
	/*
	 * 檔案上傳功能
	 * 描述 : 獲取傳送者及檔案資料，儲存檔案於server端，並且能夠供使用者預覽/存取。
	 */
	@PostMapping("/sendFile")
	public void sendFile(HttpServletResponse response, @RequestParam String username, @RequestParam MultipartFile file) throws SQLException {
		
		f.saveFile(username,file);
	
	}
	
	/*
	 * 歷史紀錄撈取
	 * 描述 : 於聊天室主頁載入時執行並回傳歷史訊息給前端渲染。
	 */
	@PostMapping("/getHistory")
	public @ResponseBody Map<String, Object> history() throws SQLException {
		
		Map<String, Object> result = databaseModel.messageLoad(conn.getConn());
		
		return result;
		
	}
	
	/*
	 * 訊息路由 - 加入聊天室
	 * 描述 : 前端connect function 完成後發送/app/join通知後端有新連線者，controller將新使用者設定至
	 * 		訊息包後轉送給STOMP代理，STOMP判斷訊息包內容後決定廣播內容。
	 */
	@MessageMapping("/join")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, 
            SimpMessageHeaderAccessor headerAccessor) {

        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;

	}
    
	/*
	 * 訊息路由 - 發送訊息
	 * 描述 : 前端於使用者觸發sendMessage function 後發送/app/chat通知後端有新訊息，controller判斷此訊息是
	 * 		一般文本或檔案，提供messageStorage不同參數來記錄訊息，記錄完成後一樣轉送STOMP代理。
	 */
    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) throws SQLException {
    			
		if(chatMessage.getFileName()!=null) {
			
			databaseModel.messageStorage(chatMessage.getSender(), chatMessage.getFilePath(), "file", conn.getConn());
			
		} else {
			
			databaseModel.messageStorage(chatMessage.getSender(), chatMessage.getContent(), "text", conn.getConn());
			
		}
		
    	return chatMessage; // 返回時會將訊息送至/topic/public
    
    }
    
}
