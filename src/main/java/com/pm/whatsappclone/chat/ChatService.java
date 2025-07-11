package com.pm.whatsappclone.chat;

import com.pm.whatsappclone.user.User;
import com.pm.whatsappclone.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByReceiverId(Authentication currentUser) {
        final String userId = currentUser.getName();
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(c -> chatMapper.toChatResponse(c, userId))
                .collect(Collectors.toList());
    }

    public String createChat(String senderId, String receiverId){
        Optional<Chat> existingChat = chatRepository.findChatByReceiverAndSenderId(senderId, receiverId);
        if (existingChat.isPresent()){
            return existingChat.get().getId();
        }

        User sender = userRepository.findUserByPublicId(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Sender user not found"));

        User receiver = userRepository.findUserByPublicId(receiverId)
                .orElseThrow(() -> new EntityNotFoundException("Sender user not found"));

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setRecipient(receiver);
        Chat savedChat = chatRepository.save(chat);
        return savedChat.getId();
    }

}
