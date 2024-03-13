package com.example.slatechatbox.chatroom.Chatroom;

import jakarta.persistence.*;

@Entity
@Table(name = "chatroom")
public class Chatroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatRoomId;

    public Chatroom() {}

    public Chatroom(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public int getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
}
