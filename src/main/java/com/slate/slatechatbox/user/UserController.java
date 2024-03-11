package com.slate.slatechatbox.user;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @ResponseBody
    @GetMapping("/user/getUserInfo/{username}")
    public User getUserInfo(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new User(user.getUsername(), user.getPassword());
        }
        return null;
    }

    @ResponseBody
    @PostMapping("/user/validateUser")
    public Boolean validateUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        User user = userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

}
