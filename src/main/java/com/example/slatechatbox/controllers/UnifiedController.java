
package com.example.slatechatbox.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.slatechatbox.models.Account;
import com.example.slatechatbox.models.AccountRepository;
import com.slate.slatechatbox.chatroom.Chatroom.Chatroom;
import com.slate.slatechatbox.chatroom.Chatroom.ChatroomRepository;
import com.slate.slatechatbox.chatroom.ChatroomMember.ChatroomMember;
import com.slate.slatechatbox.chatroom.ChatroomMember.ChatroomMemberRepository;
import com.slate.slatechatbox.message.Message;
import com.slate.slatechatbox.message.MessageRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UnifiedController {

    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private ChatroomMemberRepository chatroomMemberRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Autowired
    private MessageRepository messageRepository;

    // Login Page
    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @GetMapping("/login")
    public String getLogin(Model model, HttpSession session){
        Account account = (Account) session.getAttribute("session_account");
        if (account == null) {
            return "account/login";
        } else {
            model.addAttribute("account", account);
            return "account/home";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request) {
        if (formData.get("username") == null || formData.get("password") == null) {
            return "account/login";
        }
        List<Account> accounts = accountRepo.findByUsernameAndPassword(formData.get("username"), formData.get("password"));
        if (accounts.isEmpty()) {
            return "account/login";
        } else {
            Account account = accounts.get(0);
            request.getSession().setAttribute("session_account", account);
            model.addAttribute("account", account);
            return "account/home";
        }
    }

    // Register Page
    @GetMapping("/register")
    public String getRegister() {
        return "account/register";
    }

    @PostMapping("/account/register")
    public String registerAccount(@RequestParam Map<String, String> newaccount, HttpServletResponse response) {
        if (newaccount.get("username") == null || newaccount.get("password") == null) {
            response.setStatus(400);
            return "account/register";
        }
        if (accountRepo.findByUsername(newaccount.get("username")).size() > 0) {
            response.setStatus(409);
            return "account/register";
        }
        accountRepo.save(new Account(newaccount.get("username"),newaccount.get("password")));
        response.setStatus(201);
        return "account/login";
    }

    // Logout 
    //Sign out
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "account/logout";
    }

    // Chatroom 
    @ResponseBody
    @GetMapping("/chatroom/getChatroomIds/{username}")
    public List<Integer> getChatRoomIdsByUsername(@PathVariable String username) {
        return chatroomMemberRepository.findAllByUserName(username);
    }

    @ResponseBody
    @PostMapping("/chatroom/createChatroom")
    public void createChatroom(@RequestBody Map<String, String> body) {
        Chatroom chatroom = new Chatroom();
        chatroomRepository.save(chatroom);
        chatroomMemberRepository.save(new ChatroomMember(chatroom.getChatRoomId(), body.get("username1")));
        chatroomMemberRepository.save(new ChatroomMember(chatroom.getChatRoomId(), body.get("username2")));
    }

    // Message 
    @MessageMapping("/input/{id}")
    @SendTo("/topic/output/{id}")
    public Message message(@DestinationVariable String id, Message message) {
        return message;
    }

    @ResponseBody
    @GetMapping("/message/getMessages/{id}")
    public List<Message> getMessagesByChatRoomId(@PathVariable String id) {
        return messageRepository.findAllByChatRoomId(id);
    }

    @ResponseBody
    @PostMapping("/message/storeMessage")
    public void storeMessage(@RequestBody Map<String, String> body) {
        messageRepository.save(new Message(
                Integer.parseInt(body.get("chatRoomId")), body.get("content"), 
                body.get("sender"), body.get("timeStampMilliseconds")
        ));
    }

    // Homepage

    @GetMapping("/home")
    public String viewHomepage(Model model, HttpSession session) 
    {
        Account account = (Account) session.getAttribute("session_account");
        if (account != null) {
            model.addAttribute("account", account);
            return "account/home";
        } else {
            // If no account, redirect to the login 
            return "redirect:/login";
        }
    }

    @GetMapping("/home/chatrooms")
    public String viewUserChatrooms(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("session_account");

        if (account != null) 
        {
            List<Integer> chatroomIds = chatroomMemberRepository.findAllByUserName(account.getUsername());
            model.addAttribute("chatroomIds", chatroomIds);

            return "chatroomList";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/signout")
    public String signOut(HttpSession session, RedirectAttributes redirectAttributes) 
    {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been successfully signed out.");
        return "redirect:/login";
    }

    //post mapping for trial, not sure if it is correct
    @PostMapping("/signout")
    public String signOutPost(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logoutMessage", "You have been successfully signed out.");
        return "redirect:/login";
    }
}