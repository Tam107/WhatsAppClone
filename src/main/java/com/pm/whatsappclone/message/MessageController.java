package com.pm.whatsappclone.message;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(@RequestBody MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);
    }

    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadMedia(@RequestParam("chatId") String chatId,
                            @Parameter()
                            @RequestParam("file") MultipartFile file,
                            Authentication authentication) {
        messageService.uploadMediaMessage(chatId, file, authentication);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setMessagesToSeen(@RequestParam("chatId") String chatId, Authentication authentication) {
        messageService.setMessagesToSeen(chatId, authentication);
    }

    @GetMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable("chatId") String chatId) {
        List<MessageResponse> messages = messageService.findChatMessages(chatId);
        return ResponseEntity.ok(messages);
    }

}
