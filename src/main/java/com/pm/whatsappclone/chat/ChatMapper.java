package com.pm.whatsappclone.chat;

import com.pm.whatsappclone.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMapper {

    private final ChatRepository chatRepository;


    public ChatResponse toChatResponse(Chat c, String senderId) {
        return ChatResponse.builder()
                .id(c.getId())
                .name(c.getChatName(senderId))
                .unreadCount(c.getUnreadMessages(senderId))
                .lastMessage(c.getLastMessage())
                .isRecipientOnline(c.getRecipient().isUserOnline())
                .senderId(c.getSender().getId())
                .receiverId(c.getRecipient().getId())
                .build();
    }


}
