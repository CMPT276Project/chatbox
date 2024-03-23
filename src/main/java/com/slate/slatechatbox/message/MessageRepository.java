package com.slate.slatechatbox.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer>{
    
    @Query("SELECT m FROM Message m ORDER BY m.timeStampMilliseconds ASC")
    public List<Message> findAllChatMessages();

}
