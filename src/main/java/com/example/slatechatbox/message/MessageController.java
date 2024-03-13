package com.example.slatechatbox.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.*;
import org.springframework.web.bind.annotation.*;



@Controller
public class MessageController {

  @Autowired
  private MessageRepository messageRepository;

  @MessageMapping("/input/{id}")
  @SendTo("/topic/output/{id}")
  public Message message(@DestinationVariable String id, Message message) throws Exception {
    return message;
  }

  @ResponseBody
  @GetMapping("/message/getMessages/{id}")
  public List<Message> getMessagesByChatRoomId(@PathVariable String id) {
      List<Message> list = messageRepository.findAllByChatRoomId(id);
      return list;
  }

  @ResponseBody
  @PostMapping("/message/storeMessage")
  public void storeMessage(@RequestBody Map<String, String> body) {
      Message message = new Message(
        Integer.parseInt(body.get("chatRoomId")), body.get("content"), body.get("sender"), body.get("timeStampMilliseconds"));
      messageRepository.save(message);
  }

}
