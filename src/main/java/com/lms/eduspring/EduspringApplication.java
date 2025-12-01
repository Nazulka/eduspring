package com.lms.eduspring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

//@SpringBootApplication(scanBasePackages = "com.lms.eduspring")
//@EntityScan(basePackages = "com.lms.eduspring.model")
//@EnableJpaRepositories(basePackages = "com.lms.eduspring.repository")
@SpringBootApplication
public class EduspringApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduspringApplication.class, args);
	}

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