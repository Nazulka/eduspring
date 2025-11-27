package com.lms.eduspring.dto;

import java.time.LocalDateTime;

public record MessageDto(
        String role,
        String content,
        LocalDateTime createdAt
) {}
