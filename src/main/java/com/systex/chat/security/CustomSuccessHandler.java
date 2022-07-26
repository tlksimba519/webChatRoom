package com.systex.chat.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	ChatRoomMemberDAOImpl daoCRMember;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		System.out.println("success handler");
		
		try {
			String path = "./main.html";
			System.out.println(authentication.getName());
			ChatRoomMember user = daoCRMember.queryByUserId(authentication.getName());

			switch (user.getRole()) {

			case "Manager":
				break;

			case "Normal":
				break;

			// 阻擋串改身分的登入請求
			default:
				authentication.setAuthenticated(false);
				path = "./loginFail";
				break;
			}
			
			request.getSession().setAttribute("role", user.getRole());

			Cookie role = new Cookie("role", user.getRole());

			response.addCookie(role);

			response.setHeader("role", user.getRole());

			response.sendRedirect(path);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			authentication.setAuthenticated(false);
			
			response.sendRedirect("/loginpage");
			
		}
	}

}
