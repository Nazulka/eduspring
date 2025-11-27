package com.lms.eduspring.dto;

import java.time.LocalDateTime;

public record ConversationDto(
        Long id,
        String title,
        LocalDateTime createdAt
) {}
