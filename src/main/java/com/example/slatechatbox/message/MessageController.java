package com.example.slatechatbox.message;

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
  public List<Message> getAllMessages() {
      List<Message> list = messageRepository.findAllChatMessages();
      return list;
  }

  @ResponseBody
  @GetMapping("/message/get/{uid}")
  public List<Message> getMessageByUid(@PathVariable("uid") int uid) {
      return messageRepository.findChatMessagesByUid(uid);
  }

}
