package com.example.slatechatbox;

import java.util.Arrays;
import java.util.List;

import com.example.slatechatbox.message.Message;
import com.example.slatechatbox.message.MessageController;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageController messageController;

    @Test
    public void testGetAllMessages() throws Exception {
        List<Message> messages = Arrays.asList(
                new Message(1, "Hello", "123456789", "Alice", -1, ""),
                new Message(2, "Hi", "123456789", "Bob", -1, ""));
        String messagesString = objectMapper.writeValueAsString(messages);
        when(messageController.getAllMessages()).thenReturn(messages);

        mockMvc.perform(get("/message/getAll")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(messagesString));
    }

    @Test
    public void testGetChatMessagesByUid() throws Exception {
        List<Message> messages = Arrays.asList(
                new Message(1, "Hello", "123456789", "Alice", -1, ""),
                new Message(1, "Hi", "123456789", "Alice", -1, ""));
        String messagesString = objectMapper.writeValueAsString(messages);      
        when(messageController.getMessageByUid(1)).thenReturn(messages);
        
        mockMvc.perform(get("/message/get/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(messagesString));
    }

}