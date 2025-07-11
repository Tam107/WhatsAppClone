package com.pm.whatsappclone.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {

    private String id;

    private String name;

    private Long unreadCount;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private boolean isRecipientOnline;

    private String senderId;

    private String receiverId;
}
