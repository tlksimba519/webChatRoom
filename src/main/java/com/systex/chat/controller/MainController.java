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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.systex.chat.database.ChatModel;
import com.systex.chat.database.ConnectionObject;
import com.systex.chat.database.Database;
import com.systex.chat.file.file;
import com.systex.chat.message.ChatMessage;
import com.systex.chat.security.ChatRoomMember;
import com.systex.chat.security.ChatRoomMemberDAOImpl;
import com.systex.chat.utils.CleanStringUtil;

@Controller
public class MainController {

	@Autowired
	ChatModel chatModel;

	@Autowired
	Database dbBean;

	@Autowired
	ChatRoomMemberDAOImpl CRMemberDAOImpl;

	@Autowired
	file fileModel;

	@Autowired
	ConnectionObject conn;

	@GetMapping("/loginpage")
	public String login() {
		return "index.html";
	}

	@GetMapping("/loginFail")
	public String loginFail() {
		return "loginFail.html";
	}

	/*
	 * 註冊功能 描述 : 從註冊頁獲取使用者帳號及密碼，比對是否為未註冊帳號，註冊完畢即跳轉回登入頁。
	 */
	@PostMapping("/signup")
	public void signup(@RequestParam String UserName, @RequestParam String Password, HttpServletRequest request,
			HttpServletResponse response) throws SQLException, IOException {

//		dbBean.setID(UserName);
//		dbBean.setPasswd(Password);
//		
//		LoginStatus status = chatModel.signUp(dbBean, conn.getConn());
		ChatRoomMember crm = new ChatRoomMember();
		crm.setUserid(UserName);
		crm.setRole("Normal");
		crm.setPassword(CleanStringUtil.cleanString(Password));

		response.sendRedirect(CRMemberDAOImpl.addMember(crm).getUrl());

	}

//	/*
//	 * 登入功能
//	 * 描述 : 從登入頁獲取使用者帳號及密碼，比對資料庫中是否有對應資料，登入成功後跳轉至聊天室主頁。
//	 */
//	@PostMapping("/login")
//	public void login(@RequestParam String UserName, @RequestParam String Password, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
//		
//		dbBean.setID(UserName);
//		dbBean.setPasswd(Password);
//		
//		LoginStatus status = chatModel.login(dbBean, conn.getConn());
//		
//		response.sendRedirect(status.getUrl());
//
//	}

	/*
	 * 檔案上傳功能 描述 : 獲取傳送者及檔案資料，儲存檔案於server端，並且能夠供使用者預覽/存取。
	 */
	@PostMapping("/sendFile")
	public void sendFile(HttpServletResponse response, @RequestParam String username, @RequestParam MultipartFile file)
			throws SQLException {

		fileModel.saveFile(username, file);

	}

	/*
	 * 歷史紀錄撈取 描述 : 於聊天室主頁載入時執行並回傳歷史訊息給前端渲染。
	 */
	@PostMapping("/accessHistory")
	public @ResponseBody Map<String, Object> history(@RequestParam int historyCount) throws SQLException {

		Map<String, Object> result = chatModel.loadMessage(conn.getConn(), historyCount);

		return result;

	}

	/*
	 * 訊息路由 - 加入聊天室 描述 : 前端connect function
	 * 完成後發送/app/join通知後端有新連線者，controller將新使用者設定至
	 * 訊息包後轉送給STOMP代理，STOMP判斷訊息包內容後決定廣播內容。
	 */
	@MessageMapping("/join")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		return chatMessage;

	}

	/*
	 * 訊息路由 - 發送訊息 描述 : 前端於使用者觸發sendMessage function
	 * 後發送/app/chat通知後端有新訊息，controller判斷此訊息是
	 * 一般文本或檔案，提供storeMessage不同參數來記錄訊息，記錄完成後一樣轉送STOMP代理。
	 */
	@MessageMapping("/chat")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) throws SQLException {

		if (chatMessage.getFileName() != null) {

			chatModel.storeMessage(chatMessage.getSender(), chatMessage.getFilePath(), "file", conn.getConn());

		} else {

			chatModel.storeMessage(chatMessage.getSender(), chatMessage.getContent(), "text", conn.getConn());

		}

		return chatMessage; // 返回時會將訊息送至/topic/public

	}

}
