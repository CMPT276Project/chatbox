package com.example.slatechatbox.typingIndicator;

import java.util.ArrayList;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TypingIndicatorController {
    
    private ArrayList<String> usernameList = new ArrayList<>();

    @MessageMapping("/typing")
    @SendTo("/topic/typing")
    public ArrayList<String> typing(@Payload TypingStatus typingStatus) {
        if (!usernameList.contains(typingStatus.getUsername())) {
            usernameList.add(typingStatus.getUsername());
        }
        return usernameList;
    }

    @MessageMapping("/stopTyping")
    @SendTo("/topic/typing")
    public ArrayList<String> stopTyping(@Payload TypingStatus typingStatus) {
        if (usernameList.contains(typingStatus.getUsername())) {
            usernameList.remove(typingStatus.getUsername());
        }
        return usernameList;
    }

}
