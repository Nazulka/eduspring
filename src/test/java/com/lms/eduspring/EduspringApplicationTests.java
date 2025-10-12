package com.lms.eduspring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class EduspringApplicationTests {

	@Test
	void contextLoads() {
	}


	@Test
	void testSetup() {
		assertEquals(2, 1 + 1);
	}

}
