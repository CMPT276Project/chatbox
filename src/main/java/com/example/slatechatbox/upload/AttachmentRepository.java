package com.example.slatechatbox.upload;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
    
}
