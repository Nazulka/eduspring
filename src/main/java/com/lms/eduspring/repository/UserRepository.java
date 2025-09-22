package com.lms.eduspring.repository;

import com.lms.eduspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {}
