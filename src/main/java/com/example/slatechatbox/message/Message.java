package com.example.slatechatbox.message;

import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;
    private int chatRoomId;
    private String content;
    private String sender;
    private String timeStampMilliseconds;

    public Message() {}

    public Message(int chatRoomId, String content, String sender, String timeStampMilliseconds) {
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.sender = sender;
        this.timeStampMilliseconds = timeStampMilliseconds;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
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

    public String getTimeStampMilliseconds() {
        return timeStampMilliseconds;
    }

    public void setTimeStampMilliseconds(String timeStampMilliseconds) {
        this.timeStampMilliseconds = timeStampMilliseconds;
    }    

}
