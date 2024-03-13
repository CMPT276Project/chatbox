package com.example.slatechatbox.chatroom;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.slatechatbox.chatroom.Chatroom.Chatroom;
import com.example.slatechatbox.chatroom.Chatroom.ChatroomRepository;
import com.example.slatechatbox.chatroom.ChatroomMember.ChatroomMember;
import com.example.slatechatbox.chatroom.ChatroomMember.ChatroomMemberRepository;

@Controller
public class ChatroomController {

    @Autowired
    private ChatroomMemberRepository chatroomMemberRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @ResponseBody
    @GetMapping("/chatroom/getChatroomIds/{username}")
    public List<Integer> getChatRoomIdsByUsername(@PathVariable String username) {
        List<Integer> list = chatroomMemberRepository.findAllByUserName(username);
        return list;
    }

    @ResponseBody
    @PostMapping("/chatroom/createChatroom")
    public void createChatroom(@RequestBody Map<String, String> body) {
        Chatroom chatroom = new Chatroom();
        chatroomRepository.save(chatroom);
        ChatroomMember user1 = new ChatroomMember(chatroom.getChatRoomId(), body.get("username1"));
        chatroomMemberRepository.save(user1);
        ChatroomMember user2 = new ChatroomMember(chatroom.getChatRoomId(), body.get("username2"));
        chatroomMemberRepository.save(user2);
    }

}
