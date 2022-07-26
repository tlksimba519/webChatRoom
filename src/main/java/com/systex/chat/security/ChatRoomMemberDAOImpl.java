package com.systex.chat.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.systex.chat.database.LoginStatus;
import com.systex.chat.utils.CleanStringUtil;

@Component
public class ChatRoomMemberDAOImpl implements ChatRoomMemberDAO {
	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<ChatRoomMember> querySHLMemberList() throws Exception {
		String sql = "select * from ChatRoomMember";

		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ChatRoomMember.class));
	}

	@Override
	public ChatRoomMember queryByUserId(String userId) throws Exception {

		String sql = "select * from ChatRoomMember where userid = ?";
		try {
			return (ChatRoomMember) jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ChatRoomMember.class),
					userId);

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public LoginStatus addMember(ChatRoomMember crm) {
		try {
			String sql = "insert into ChatRoomMember (userid, role,password) values (?,?,?)";

			// 新增帳戶時需要進行加密與字串檢核
			jdbcTemplate.update(sql, crm.getUserid(), crm.getRole(),
					new BCryptPasswordEncoder().encode(CleanStringUtil.cleanString(crm.getPassword())));
			return LoginStatus.SignupSuccess;
		} catch (Exception e) {
			e.printStackTrace();
			return LoginStatus.Error;
		}
	}

	@Override
	public int deleteByName(String userId) throws Exception {

		String sql = "delete from ChatRoomMember where userid = ?";

		return jdbcTemplate.update(sql, userId);
	}

	@Override
	public int updateMember(ChatRoomMember crm) throws Exception {
		// TODO Auto-generated method stub
		String sql = "update ChatRoomMember set role = ?, password= ? where userid = ?";

		return jdbcTemplate.update(sql, crm.getRole(), crm.getPassword(), crm.getUserid());
	}
}
