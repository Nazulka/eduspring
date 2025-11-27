package com.lms.eduspring.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationDetailDto(
        Long id,
        String title,
        LocalDateTime createdAt,
        List<MessageDto> messages
) {}
