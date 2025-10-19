package com.lms.eduspring;

import com.lms.eduspring.model.User;
import com.lms.eduspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EduspringApplication implements CommandLineRunner {

	@Autowired
	private UserService userService; // <-- Spring injects the service

	public static void main(String[] args) {
		SpringApplication.run(EduspringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Runs AFTER Spring Boot has initialized all beans

		User user = new User(
				"student1",
				"mypassword",
				"Alice",
				"Smith",
				"alice@example.com",
				"STUDENT"
		);

		// Use UserService to hash and save the user
		userService.registerUser(user);

		System.out.println("Test user registered: " + user.getUsername());
	}
}
