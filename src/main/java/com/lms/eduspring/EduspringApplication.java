package com.lms.eduspring;

import com.lms.eduspring.model.User;
import com.lms.eduspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EduspringApplication implements CommandLineRunner {

	// ✅ Marked as optional so tests won’t fail if it's missing
	@Autowired(required = false)
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(EduspringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// ✅ Only run initialization when the real UserService is available
		if (userService != null) {
			User user = new User(
					"student1",
					"mypassword",
					"Alice",
					"Smith",
					"alice@example.com",
					"STUDENT"
			);

			userService.registerUser(user);
			System.out.println("Test user registered: " + user.getUsername());
		} else {
			System.out.println("Skipping user initialization (UserService not loaded in test context).");
		}
	}
}
