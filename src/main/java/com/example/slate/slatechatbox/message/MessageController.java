package com.example.slate.slatechatbox.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.*;
import org.springframework.web.bind.annotation.*;



@Controller
public class MessageController {

  @Autowired
  private MessageRepository messageRepository;

  @MessageMapping("/input")
  @SendTo("/topic/output")
  public Message message(Message message) throws Exception {
    messageRepository.save(message);
    return message;
  }

  @ResponseBody
  @GetMapping("/message/getAll")
  public List<Message> getMessagesByChatRoomId() {
      List<Message> list = messageRepository.findAllChatMessages();
      return list;
  }

}
