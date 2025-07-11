package com.pm.whatsappclone.message;

import com.pm.whatsappclone.chat.Chat;
import com.pm.whatsappclone.chat.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper mapper;

    public void save(MessageRequest messageReq){
        Chat chat = chatRepository.findById(messageReq.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id: " + messageReq.getChatId()));

        Message message = new Message();
        message.setContent(messageReq.getContent());
        message.setSenderId(messageReq.getSenderId());
        message.setReceiverId(messageReq.getReceiverId());
        message.setType(messageReq.getType());
        message.setState(MessageState.SENT);
        message.setChat(chat);

        messageRepository.save(message);

        // todo // notify the recipient about the new message

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
        
        //todo notify the sender that the messages have been seen

    }
    
    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication){
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(()-> new EntityNotFoundException("Chat not found with id: " + chatId));
        
        final String sendId = getSenderId(chat, authentication);
        final String recipientId = getRecipientId(chat, authentication);

        final String filePath = fileService.saveFile(file, sendId);
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
