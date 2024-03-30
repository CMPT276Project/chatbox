package com.example.slatechatbox.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.example.slatechatbox.storage.StorageFileNotFoundException;
import com.example.slatechatbox.storage.StorageService;

@Controller
public class FileUploadController {

	@Autowired
	private StorageService storageService;

	@Autowired
	private SimpMessagingTemplate template;

	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("uid") String uid, @RequestParam("message") String message,
			@RequestParam("senderName") String senderName,
			@RequestParam("timeStampMilliseconds") String timeStampMilliseconds) {
		storageService.store(file);
		sendFile(file.getOriginalFilename(), message, uid, senderName, timeStampMilliseconds);
		return ResponseEntity.ok("You successfully uploaded " + file.getOriginalFilename() + "!");
	}

	public void sendFile(String filename, String message, String uid, String senderName, String timeStampMilliseconds) {
		Resource file = storageService.loadAsResource(filename);
		if (file == null) {
			throw new StorageFileNotFoundException("Could not find file: " + filename);
		}
		String fileUri = MvcUriComponentsBuilder.fromMethodName(
				FileUploadController.class,
				"serveFile",
				filename)
				.build()
				.toUri()
				.toString();
		File fileObj = new File(fileUri, message, Integer.parseInt(uid), senderName, timeStampMilliseconds);

		// Send a WebSocket notification to the "/topic/file/output" topic
		template.convertAndSend("/topic/output/file", fileObj);
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);

		if (file == null)
			return ResponseEntity.notFound().build();

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
