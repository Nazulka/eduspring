package com.lms.eduspring;

import com.lms.eduspring.model.User;
import com.lms.eduspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EduspringApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository; // inject repository

	public static void main(String[] args) {
		SpringApplication.run(EduspringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// This runs AFTER Spring Boot starts
		User user = new User(
				"student1",
				"hashedPassword123",
				"Alice",
				"Smith",
				"alice@example.com",
				"STUDENT"
		);
		userRepository.save(user); // auto-generates UUID ID
		System.out.println("Test user inserted!");
	}
}
