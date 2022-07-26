package com.systex.chat.security;

import com.systex.chat.database.LoginStatus;

public interface ChatRoomMemberDAO {

	public ChatRoomMember queryByUserId(String userId) throws Exception;

	public LoginStatus addMember(ChatRoomMember shlMember) throws Exception;

	public int deleteByName(String username) throws Exception;

	public int updateMember(ChatRoomMember shlMember) throws Exception;
}
