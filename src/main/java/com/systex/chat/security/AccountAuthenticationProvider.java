package com.systex.chat.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.systex.chat.utils.CleanStringUtil;

@Component
public class AccountAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	ChatRoomMemberDAOImpl daoCRMember;

	@Autowired
	private CustomLoginService userDetailsService;

//	@Autowired
//	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String userId = authentication.getName();
		String password = CleanStringUtil.cleanString((String) authentication.getCredentials());

		System.out.println("before:" + userId + " " + password);

		ChatRoomMember user = null;
		try {
			user = daoCRMember.queryByUserId(userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("after:" + user.getUserid() + " " + user.getPassword());

		UserDetails userInfo = userDetailsService.loadUserByUsername(userId);
		if (userInfo == null) {
			throw new BadCredentialsException("用戶名不存在");
		}
//		boolean flag = passwordEncoder.matches(password, userInfo.getPassword());
//		if (!flag) {
//			throw new BadCredentialsException("密碼不正確");
//		}
		Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();

		return new UsernamePasswordAuthenticationToken(userInfo, password, authorities);

		// return super.authenticate(authentication);
	}

	@Override
	public boolean supports(Class<?> authentication) {

		return true;
	}
}