package com.backend.curi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(locations = "classpath:application-data.properties")
@SpringBootTest
class CuriApplicationTests {

	@Test
	void contextLoads() {
	}

}
