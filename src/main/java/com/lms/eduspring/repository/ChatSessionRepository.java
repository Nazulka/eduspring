package com.lms.eduspring.repository;

import com.lms.eduspring.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUser_Id(Long userId);
    Optional<ChatSession> findByIdAndUser_Id(Long id, Long userId);

}

