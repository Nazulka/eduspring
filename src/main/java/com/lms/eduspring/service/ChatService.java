package com.lms.eduspring.service;

import com.lms.eduspring.dto.ConversationDetailDto;
import com.lms.eduspring.dto.ConversationDto;
import com.lms.eduspring.model.ChatMessage;
import com.lms.eduspring.model.ChatSession;

import java.util.List;

public interface ChatService {

    ChatSession processUserMessage(Long userId, Long conversationId, String content);

    void processAiMessage(Long conversationId, String content);

    List<ChatSession> getSessionsForUser(Long userId);

    List<ChatMessage> getMessagesForUserSession(Long userId, Long conversationId);

    List<ConversationDto> getConversationDtosForUser(Long userId);

    ConversationDetailDto getConversationDetail(Long userId, Long conversationId);


}




