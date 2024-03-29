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

    public Message() {}

    public Message(int uid, String content, String timeStampMilliseconds, String senderName) {
        this.uid = uid;
        this.content = content;
        this.timeStampMilliseconds = timeStampMilliseconds;
        this.senderName = senderName;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStampMilliseconds() {
        return timeStampMilliseconds;
    }

    public void setTimeStampMilliseconds(String timeStampMilliseconds) {
        this.timeStampMilliseconds = timeStampMilliseconds;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

}
