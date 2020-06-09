package com.systex.chat.message;

/*
 * 聊天訊息容器
 */
public class ChatMessage {

	private ChatType type;

	private String sender;

	private String content;

	private String filePath;

	private String fileName;

	private String fileType;

	public enum ChatType{

		CHAT,
		JOIN,
		LEAVE,
		FILE

	}

	public ChatType getType() {

		return type;

	}

	public void setType(ChatType type) {

		this.type = type;

	}

	public String getContent() {

		return content;

	}

	public void setContent(String content) {

		this.content = content;

	}

	public String getSender() {

		return sender;

	}

	public void setSender(String sender) {

		this.sender = sender;

	}

	public String getFilePath() {

		return filePath;

	}

	public void setFilePath(String filePath) {

		this.filePath = filePath;

	}

	public String getFileType() {

		return fileType;

	}

	public void setFileType(String fileType) {

		this.fileType = fileType;

	}

	public String getFileName() {

		return fileName;

	}

	public void setFileName(String fileName) {

		this.fileName = fileName;

	}

}