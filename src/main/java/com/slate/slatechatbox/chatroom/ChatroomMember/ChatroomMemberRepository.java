package com.slate.slatechatbox.chatroom.ChatroomMember;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Integer>{
    
    @Query("SELECT cm.chatRoomId FROM ChatroomMember cm WHERE cm.userName = :username")
    List<Integer> findAllByUserName(@Param("username") String username);

}
