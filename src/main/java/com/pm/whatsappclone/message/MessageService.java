package com.pm.whatsappclone.message;

import com.pm.whatsappclone.chat.Chat;
import com.pm.whatsappclone.chat.ChatRepository;
import com.pm.whatsappclone.file.FileService;
import com.pm.whatsappclone.file.FileUtils;
import com.pm.whatsappclone.notification.Notification;
import com.pm.whatsappclone.notification.NotificationService;
import com.pm.whatsappclone.notification.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    public void saveMessage(MessageRequest messageReq){
        Chat chat = chatRepository.findById(messageReq.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id : " + messageReq.getChatId()));

        Message message = new Message();
        message.setContent(messageReq.getContent());
        message.setSenderId(messageReq.getSenderId());
        message.setReceiverId(messageReq.getReceiverId());
        message.setType(messageReq.getType());
        message.setState(MessageState.SENT);
        message.setChat(chat);

        messageRepository.save(message);

        // send notification to the recipient
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .messageType(message.getType())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .type(NotificationType.MESSAGE)
                .chatName(chat.getChatName(message.getSenderId()))
                .build();

        notificationService.sendNotification(message.getReceiverId(), notification);

    }

    public List<MessageResponse> findChatMessages(String chatId){
        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(mapper::toMessageResponse)
                .toList();
    }

    @Transactional
    public void setMessagesToSeen(String chatId, Authentication authentication){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id: " + chatId));
        final String recipientId = getRecipientId(chat, authentication);

        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);
        
        // notify the sender that the messages have been seen
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .senderId(getSenderId(chat, authentication))
                .receiverId(recipientId)
                .type(NotificationType.SEEN)
                .build();

        notificationService.sendNotification(recipientId, notification);

    }
    
    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(()-> new EntityNotFoundException("Chat not found with id: " + chatId));
        
        final String sendId = getSenderId(chat, authentication);
        final String recipientId = getRecipientId(chat, authentication);

        final String filePath = fileService.saveFile(file, sendId);

        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(sendId);
        message.setReceiverId(recipientId);
        message.setType(MessageType.IMAGE);
        message.setState(MessageState.SEEN);
        message.setFileUrl(filePath);
        messageRepository.save(message);

        // notify the recipient about the new media message
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .type(NotificationType.IMAGE)
                .messageType(MessageType.IMAGE)
                .senderId(sendId)
                .receiverId(recipientId)
                .image(FileUtils.readFileFromLocation(filePath))
                .build();

        notificationService.sendNotification(recipientId, notification);
    }

    private String getSenderId(Chat chat, Authentication authentication) {
        if (chat.getSender().getId().equals(authentication.getName())){
            return chat.getSender().getId();
        }

        return chat.getRecipient().getId();
    }

    private String getRecipientId(Chat chat, Authentication authentication) {
        if (chat.getSender().getId().equals(authentication.getName())){
            return chat.getRecipient().getId();
        }

        return chat.getSender().getId();
    }

}
