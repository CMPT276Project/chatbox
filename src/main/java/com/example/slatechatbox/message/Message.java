package com.example.slatechatbox.message;

import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;
    private int uid;
    private String senderName;
    private String timeStampMilliseconds;
    private String content;
    private int fileId;
	private String fileName;

    public Message() {}

	public Message(int uid, String senderName, String timeStampMilliseconds, String content, int fileId, String fileName) {
		this.uid = uid;
		this.senderName = senderName;
		this.timeStampMilliseconds = timeStampMilliseconds;
		this.content = content;
		this.fileId = fileId;
		this.fileName = fileName;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getTimeStampMilliseconds() {
		return timeStampMilliseconds;
	}

	public void setTimeStampMilliseconds(String timeStampMilliseconds) {
		this.timeStampMilliseconds = timeStampMilliseconds;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
    
}
