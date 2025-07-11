package com.pm.whatsappclone.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Finds messages by the chat ID.
     *
     * @param chatId the ID of the chat
     * @return a list of messages associated with the specified chat ID
     */
    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(String chatId);

    @Modifying
    @Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
    void setMessagesToSeenByChatId(@Param("chatId") String chatId,@Param("newState") MessageState messageState);
}
