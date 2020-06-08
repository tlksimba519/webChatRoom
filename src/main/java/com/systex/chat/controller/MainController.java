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
	
	@PostMapping("/signup")
	public void signup(@RequestParam String UserName,@RequestParam String Password,HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {

		boolean status;
		
		databaseBean.setID(UserName);
		databaseBean.setPasswd(Password);
		
		conn.setConn(databaseModel.getConnetion());
		status = databaseModel.signup(databaseBean,conn.getConn());
		databaseModel.logout(conn.getConn());
		
		if(status) {
			
			response.sendRedirect("/index.html");
			
		}
		
		else {
				
			response.sendRedirect("/error.html");
				
		}

	}
	
	@PostMapping("/login")
	public void login(@RequestParam String UserName,@RequestParam String Password,HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {

		boolean status;
		
		databaseBean.setID(UserName);
		databaseBean.setPasswd(Password);
		conn.setConn(databaseModel.getConnetion());
		
		status = databaseModel.login(databaseBean,conn.getConn());
		
		if(status) {
			
			response.sendRedirect("/main.html");
			
		}
		
		else {
				
			response.sendRedirect("/fail.html");
				
		}

	}
	
	@PostMapping("/sendFile")
	public void sendFile(HttpServletResponse response ,@RequestParam String username,@RequestParam MultipartFile file) throws SQLException {
		
		f.save(username,file);
	
	}
	
	@PostMapping("/getHistory")
	public @ResponseBody Map<String, Object> history() throws SQLException {
		
		Map<String, Object> result = databaseModel.load(conn.getConn());
		
		return result;
		
	}
	
	@MessageMapping("/join")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, 
            SimpMessageHeaderAccessor headerAccessor) {

        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;

	}
    
    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) throws SQLException {
    			
		if(chatMessage.getFileName()!=null) {
			
			databaseModel.storage(chatMessage.getSender(),chatMessage.getFilePath(),"file",conn.getConn());
			
		} else {
			
			databaseModel.storage(chatMessage.getSender(),chatMessage.getContent(),"text",conn.getConn());
			
		}
		
    	return chatMessage; // 返回時會將訊息送至/topic/public
    
    }
    
}
