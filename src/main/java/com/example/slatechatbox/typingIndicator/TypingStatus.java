package com.example.slatechatbox.typingIndicator;

public class TypingStatus {
    
    String username;
    boolean typing;

    public TypingStatus() {}

    public TypingStatus(String username, boolean typing) {
        this.username = username;
        this.typing = typing;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isTyping() {
        return typing;
    }

    public void setTyping(boolean typing) {
        this.typing = typing;
    }
}
