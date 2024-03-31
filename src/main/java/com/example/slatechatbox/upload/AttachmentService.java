package com.example.slatechatbox.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;
    
    public Attachment saveAttachment(MultipartFile file) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if(fileName.contains("..")) {
                throw new Exception("Filename contains invalid path" + fileName);
            }
            Attachment attachment = new Attachment(fileName, file.getContentType(), file.getBytes());
            return attachmentRepository.save(attachment);
        } catch (Exception e) {
            throw new Exception("Could not store file " + fileName);
        }
    }

    public Attachment getAttachment(int id) throws Exception {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new Exception("Attachment not found"));
    }

}
