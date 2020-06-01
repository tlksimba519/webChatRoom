package com.systex.chat.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import org.springframework.web.multipart.MultipartFile;

import com.systex.chat.database.Database;
import com.systex.chat.database.DatabaseModel;
import com.systex.chat.file.file;
import com.systex.demo.message.ChatMessage;

@Controller
public class MainController {

	@Autowired
	Database d ;
	DatabaseModel dm;
	file f;
	
	@PostMapping("/login")
	public void login(@RequestParam String UserName,@RequestParam String Password,HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
		//request.getSession().setAttribute("dbUser",request.getParameter("UserName"));
		//request.getSession().setAttribute("dbPasswd",request.getParameter("Password"));

		boolean status;
		
		d.setID(UserName);
		d.setPasswd(Password);
	
		Connection conn = dm.getConnetion();
		status = dm.login(d,conn);
			
		if(status) {
			request.getSession().setAttribute("isLogin",UserName);
			response.sendRedirect("/main.html");
			
		}
		
		else {
				
			response.sendRedirect("/error.html");
				
		}

	}
	
	@PostMapping("/sendFile")
	public void sendFile(HttpServletResponse response ,@RequestParam String username,@RequestParam MultipartFile file) throws SQLException {
		
		f.save(username,file);
	
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
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    	/*if(chatMessage.getFileName()!=null) {
    		f.setContent(chatMessage.getContent());
    		f.setFile(chatMessage.getFileName());
    		f.setSender(chatMessage.getSender());
    		f.save();
    		
    	}*/
    	return chatMessage; // 返回時會將訊息送至/topic/public
    
    }
    
}
