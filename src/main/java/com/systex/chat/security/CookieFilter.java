package com.systex.chat.security;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

/*
 * 建立Cookie保存使用者登入狀態
 */
@Component
public class CookieFilter extends GenericFilterBean {

	@Autowired
	ChatRoomMemberDAOImpl daoCRMember;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String userId = request.getParameter("userId");
		if (userId != null) {

			try {

				ChatRoomMember user = null;

				user = daoCRMember.queryByUserId(userId);

				if (user == null) {
					resp.sendRedirect("loginFail");
					return;
				}

				String roleStr = user.getRole();

				String regex = "[`~!@#$%^&*()\\+\\=\\{}|:\"?><【】\\\r\\\n]";

				Pattern pa = Pattern.compile(regex);

				Matcher ma = pa.matcher(roleStr);

				if (ma.find()) {
					roleStr = ma.replaceAll("");
				}

				String roleStrClean = roleStr;

				req.getSession().setAttribute("role", roleStrClean);

				Cookie role = new Cookie("role", user.getRole());

				resp.addCookie(role);

				resp.setHeader("role", roleStrClean);

			} catch (Exception e) {
				resp.sendRedirect("loginFail");
				return;
			}
		}
		chain.doFilter(req, resp);
	}

}
