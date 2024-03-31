package com.example.slatechatbox.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties.Servlet;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
    public void uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("uid") String uid, @RequestParam("message") String message,
			@RequestParam("senderName") String senderName,
			@RequestParam("timeStampMilliseconds") String timeStampMilliseconds) throws Exception {
        Attachment attachment = null;
        String downloadURL = "";
        attachment = attachmentService.saveAttachment(file);
        downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(Integer.valueOf(attachment.getId()).toString())
                .toUriString();

        Message messageObj = new Message(Integer.parseInt(uid), message, timeStampMilliseconds, senderName);
        ResponseData fileData = new ResponseData(attachment.getFileName(), downloadURL, attachment.getFileType(), attachment.getData().length, messageObj);
        sendFile(fileData, message, uid, senderName, timeStampMilliseconds);
    }

    public void sendFile(ResponseData data, String message, String uid, String senderName, String timeStampMilliseconds) {
        messageRepository.save(new Message(Integer.parseInt(uid), message, timeStampMilliseconds, senderName));
        template.convertAndSend("/topic/output/file", data);
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
