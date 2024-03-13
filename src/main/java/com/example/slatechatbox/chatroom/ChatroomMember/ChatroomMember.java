package com.example.slatechatbox.chatroom.ChatroomMember;

import jakarta.persistence.*;

@Entity
@Table(name = "chatmember")
public class ChatroomMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int chatRoomId;
    private String userName;

    public ChatroomMember() {}

    public ChatroomMember(int chatRoomId, String userName) {
        this.chatRoomId = chatRoomId;
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
