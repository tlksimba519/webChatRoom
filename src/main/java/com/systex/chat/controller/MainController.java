package com.systex.chat.controller;

import java.io.IOException;
import java.sql.Connection;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.systex.chat.database.Database;
import com.systex.chat.database.DatabaseModel;
import com.systex.chat.file.file;
import com.systex.chat.message.ChatMessage;

@Controller
public class MainController {

	@Autowired
	Database d;
	DatabaseModel dm;
	file f;
	
	@PostMapping("/signup")
	public void signup(@RequestParam String UserName,@RequestParam String Password,HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {

		boolean status;
		
		d.setID(UserName);
		d.setPasswd(Password);
	
		Connection conn = dm.getConnetion();
		status = dm.signup(d,conn);
		dm.logout(conn);
		
		if(status) {
			
			response.sendRedirect("/index.html");
			
		}
		
		else {
				
			response.sendRedirect("/error.html");
				
		}

	}
	
	@PostMapping("/login")
	public void login(@RequestParam String UserName,@RequestParam String Password,HttpServletRequest request,HttpServletResponse response) throws SQLException, IOException {
		//request.getSession().setAttribute("dbUser",request.getParameter("UserName"));
		//request.getSession().setAttribute("dbPasswd",request.getParameter("Password"));

		boolean status;
		
		d.setID(UserName);
		d.setPasswd(Password);
	
		Connection conn = dm.getConnetion();
		status = dm.login(d,conn);
		dm.logout(conn);
		
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
	
	@GetMapping("/getHistory")
	public @ResponseBody Map<String, Object> history() throws SQLException {
		
		Connection conn = dm.getConnetion();
		dm.login(d,conn);
		Map<String, Object> result = dm.load();
		dm.logout(conn);
		
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
    	
    	Connection conn = dm.getConnetion();
		dm.login(d,conn);
		
		if(chatMessage.getFileName()!=null) {
			
			dm.storage(chatMessage.getSender(),chatMessage.getFilePath(),"file");
			
		} else {
			
			dm.storage(chatMessage.getSender(),chatMessage.getContent(),"text");
			
		}
		
		dm.logout(conn);
    	return chatMessage; // 返回時會將訊息送至/topic/public
    
    }
    
}
