package com.lms.eduspring;

import com.lms.eduspring.model.User;
import com.lms.eduspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class EduspringApplication implements CommandLineRunner {

	@Value("${JWT_SECRET:NOT_FOUND}")
	private String jwtSecret;

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

//	remove later for prod
	@Component
	public class PropertyDebugRunner implements CommandLineRunner {

		@Value("${JWT_SECRET:NOT_FOUND}")
		private String jwtSecret;

		@Value("${OPENAI_API_KEY:NOT_FOUND}")
		private String openaiKey;

		@Override
		public void run(String... args) {
			System.out.println("🔍 JWT_SECRET loaded? " + !jwtSecret.equals("NOT_FOUND"));
			System.out.println("🔍 OPENAI_API_KEY loaded? " + !openaiKey.equals("NOT_FOUND"));
		}
	}
}


