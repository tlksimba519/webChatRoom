package com.systex.chat.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomLoginService implements UserDetailsService {
	@Autowired
	private ChatRoomMemberDAOImpl daoCRMember;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		try {
			// 驗證使用者是否存在並建立User物件提供給Security使用
			ChatRoomMember user = daoCRMember.queryByUserId(userId);
			return new User(user.getUserid(), user.getPassword(), Collections.emptyList());
		} catch (Exception e) {
			throw new UsernameNotFoundException("Username is wrong.");
		}

	}

}
