package com.helalferrari.kitnetsapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {"api.security.token.secret=segredo-teste-application-tests-123"})
class KitnetsApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
