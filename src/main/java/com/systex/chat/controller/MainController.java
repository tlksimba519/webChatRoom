package com.systex.chat.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.systex.chat.database.Database;
import com.systex.chat.database.DatabaseModel;

@Controller
public class MainController {

	@Autowired
	Database d ;
	DatabaseModel dm;
	
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
}
