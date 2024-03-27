package com.example.slatechatbox.account;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.example.slatechatbox.message.Message;
import com.example.slatechatbox.message.MessageRepository;

@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("login");
    }

    @GetMapping("/login")
    public String getLogin(Model model, HttpServletResponse response, HttpSession session) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader(HttpHeaders.PRAGMA, "no-cache"); // HTTP 1.0
        response.setDateHeader(HttpHeaders.EXPIRES, 0); // Proxies
        System.out.println("login page");
        Account account = (Account) session.getAttribute("session_account");
        if (account == null) {
            System.out.println("null account");
            return "account/login";
        } else {
            model.addAttribute("account", account);
            return "account/home";
        }
    }

    @GetMapping("/home")
    public String getHome(Model model, HttpServletResponse response, HttpSession session) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader(HttpHeaders.PRAGMA, "no-cache"); // HTTP 1.0
        response.setDateHeader(HttpHeaders.EXPIRES, 0); // Proxies
        System.out.println("home page");
        Account account = (Account) session.getAttribute("session_account");
        if (account == null) {
            System.out.println("null account");
            return "account/login";
        } else {
            model.addAttribute("account", account);
            return "account/home";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> formData, Model model, HttpServletRequest request,
            HttpSession session, HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader(HttpHeaders.PRAGMA, "no-cache"); // HTTP 1.0
        response.setDateHeader(HttpHeaders.EXPIRES, 0); // Proxies
        if (formData.get("username") == null || formData.get("password") == null) {
            return "account/login";
        }
        System.out.println("login post request");
        String username = formData.get("username");
        String password = formData.get("password");
        List<Account> accounts = accountRepo.findByUsernameAndPassword(username, password);
        if (accounts.isEmpty()) {
            return "account/login";
        } else {
            Account account = accounts.get(0);
            request.getSession().setAttribute("session_account", account);
            model.addAttribute("account", account);
            return "redirect:/home";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        System.out.println("logout");
        request.getSession().invalidate();
        return "account/logout";

    }

    @GetMapping("/register")
    public String getRegister() {
        return "account/register";
    }

    @PostMapping("/account/register")
public String registerAccount(@RequestParam Map<String, String> newaccount, Model model, HttpServletResponse response) {
    if (newaccount.get("username") == null || newaccount.get("password") == null) {
        response.setStatus(400);
        return "account/register";
    }
    if (accountRepo.findByUsername(newaccount.get("username")).size() > 0) {
        response.setStatus(409);
        model.addAttribute("error", "Username is already in use.");
        return "account/register";
    }
    String username = newaccount.get("username");
    String password = newaccount.get("password");
    accountRepo.save(new Account(username,password));
    System.out.println("new account saved");
    response.setStatus(201);
    model.addAttribute("success", "Registration successful!");
    return "account/register";
}

    @GetMapping("/chatroom")
    public String getChatroom(Model model, HttpServletResponse request, HttpSession session) {
        Account account = (Account) session.getAttribute("session_account");
        model.addAttribute("account", account);
        return "account/chatroomPage";
    }

    @GetMapping("/history")
    public String getHistory(Model model, HttpServletResponse request, HttpSession session) {
        Account account = (Account) session.getAttribute("session_account");
        List<Message> messages = messageRepository.findAllChatMessages();
        model.addAttribute("messages",messages);
        model.addAttribute("account", account);
        return "account/history";
    }

}
