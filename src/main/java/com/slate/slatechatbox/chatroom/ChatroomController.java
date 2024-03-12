package com.slate.slatechatbox.chatroom;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.slate.slatechatbox.chatroom.ChatroomMember.ChatroomMemberRepository;
import com.slate.slatechatbox.chatroom.ChatroomMember.ChatroomMember;
import com.slate.slatechatbox.chatroom.Chatroom.ChatroomRepository;
import com.slate.slatechatbox.chatroom.Chatroom.Chatroom;
import org.springframework.web.bind.annotation.*;

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
