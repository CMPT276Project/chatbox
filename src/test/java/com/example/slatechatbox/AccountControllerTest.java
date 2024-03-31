package com.example.slatechatbox;

import com.example.slatechatbox.account.Account;
import com.example.slatechatbox.account.AccountController;
import com.example.slatechatbox.account.AccountRepository;
import com.example.slatechatbox.message.MessageRepository;
import com.example.slatechatbox.message.Message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private MessageRepository messageRepository;

    @Test
    public void testProcess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("login"));
    }

    @Test
    public void testGetLoginNull() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(view().name("account/login"));
    }

    @Test
    public void testGetLoginNotNull() throws Exception {
        Account account = new Account();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", account);

        mockMvc.perform(get("/login").session(session))
                .andExpect(view().name("account/home"))
                .andExpect(model().attribute("account", account));
    }

    @Test
    public void testGetHomeNull() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(view().name("account/login"));
    }

    @Test
    public void testGetHomeNotNull() throws Exception {
        Account account = new Account();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", account);

        mockMvc.perform(get("/home").session(session))
                .andExpect(view().name("account/home"))
                .andExpect(model().attribute("account", account));
    }

    @Test
    public void testLoginNullUsername() throws Exception {
        mockMvc.perform(post("/login")
                .param("password", ""))
                .andExpect(view().name("account/login"));
    }

    @Test
    public void testLoginNullPassword() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", ""))
                .andExpect(view().name("account/login"));
    }

    @Test
    public void testLoginNullForm() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(view().name("account/login"));
    }

    @Test
    public void testLoginWithInvalidCredentials() throws Exception {
        when(accountRepository.findByUsernameAndPassword("test", "test"))
                .thenReturn(Collections.emptyList());
                
        mockMvc.perform(post("/login")
                .param("username", "test")
                .param("password", "test"))
                .andExpect(view().name("account/login"));
    }

    @Test
    public void testLoginWithValidCredentials() throws Exception {
        Account account = new Account();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", account);
        when(accountRepository.findByUsernameAndPassword("test", "test")).thenReturn(Arrays.asList(account));

        mockMvc.perform(post("/login").session(session)
                .param("username", "test")
                .param("password", "test"))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/home"));
    }

    @Test
    public void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", "test");

        mockMvc.perform(get("/logout").session(session))
                .andExpect(view().name("account/logout"))
                .andExpect(request().sessionAttributeDoesNotExist("session_account"));
    }

    @Test
    public void testGetRegister() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(view().name("account/register"));
    }

    @Test
    public void testRegisterAccountNullUsername() throws Exception {
        mockMvc.perform(post("/account/register"))
                .andExpect(view().name("account/register"));
    }

    @Test
    public void testRegisterAccountNullPassword() throws Exception {
        mockMvc.perform(post("/account/register")
                .param("username", "test"))
                .andExpect(view().name("account/register"));
    }

    @Test
    public void testRegisterAccountNullParameters() throws Exception {
        mockMvc.perform(post("/account/register"))
                .andExpect(view().name("account/register"));
    }

    @Test
    public void testRegisterAccountExistingUsername() throws Exception {
        when(accountRepository.findByUsername("test")).thenReturn(Arrays.asList(new Account()));

        mockMvc.perform(post("/account/register")
                .param("username", "test"))
                .andExpect(view().name("account/register"));
    }

    @Test
    public void testRegisterAccountValidCredentials() throws Exception {
        when(accountRepository.findByUsername("test")).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/account/register")
                .param("username", "test")
                .param("password", "test"))
                .andExpect(view().name("account/register"));
    }

    @Test
    public void testGetChatroom() throws Exception {
        Account account = new Account("username", "password");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", account);

        mockMvc.perform(get("/chatroom").session(session))
                .andExpect(view().name("account/chatroomPage"))
                .andExpect(model().attribute("account", account));
    }

    @Test
    public void testGetHistory() throws Exception {
        Message message1 = new Message(1, "Hello", "123456789", "Alice", -1, "");
        Message message2 = new Message(1, "World", "123456790", "Alice", -1, "");
        Message message3 = new Message(2, "Hi", "123456789", "Bob", -1, "");
        List<Message> messages = Arrays.asList(message1, message2, message3);
        when(messageRepository.findAllChatMessages()).thenReturn(messages);
        Account account = new Account();
        account.setUid(1);
        account.setUsername("Alice");
        account.setPassword("password");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("session_account", account);
        
        mockMvc.perform(get("/history").session(session))
                .andExpect(view().name("account/history"))
                .andExpect(model().attribute("account", account))
                .andExpect(model().attribute("messages", messages));
    }

}