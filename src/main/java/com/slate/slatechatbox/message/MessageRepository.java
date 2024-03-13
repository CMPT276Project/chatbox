package com.slate.slatechatbox.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer>{
    
    @Query("SELECT m FROM Message m WHERE m.chatRoomId = :chatRoomId ORDER BY m.timeStampMilliseconds ASC")
    public List<Message> findAllByChatRoomId(@Param("chatRoomId") String chatRoomId);

}
