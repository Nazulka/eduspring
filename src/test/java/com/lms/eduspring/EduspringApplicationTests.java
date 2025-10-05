package com.lms.eduspring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class EduspringApplicationTests {

	@Test
	void contextLoads() {
	}


	@Test
	void testSetup() {
		assertEquals(2, 1 + 1);
	}

}
