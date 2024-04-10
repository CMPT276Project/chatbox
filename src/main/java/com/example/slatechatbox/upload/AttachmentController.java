package com.example.slatechatbox.upload;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.core.io.ByteArrayResource;
import com.example.slatechatbox.message.Message;
import com.example.slatechatbox.message.MessageRepository;

@RestController
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate template;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestBody Map<String, String> body, @RequestParam("file") MultipartFile file) throws Exception {
        Attachment attachment = attachmentService.saveAttachment(file);
        Integer fileId = Integer.valueOf(attachment.getId());
        Message messageObj = new Message(
            Integer.parseInt(body.get("uid")), body.get("senderName"), 
            body.get("timeStampMilliseconds"), body.get("content"), fileId, attachment.getFileName());
        messageRepository.save(messageObj);
        template.convertAndSend("/topic/output", messageObj);
        return ResponseEntity.ok().body("File uploaded successfully");
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable int id) throws Exception {
        Attachment attachment = null;
        attachment = attachmentService.getAttachment(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

}
