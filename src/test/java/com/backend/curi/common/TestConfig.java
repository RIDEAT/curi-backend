package com.backend.curi.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@Profile("build")
@TestPropertySource("classpath:application-data-build.properties")
public class TestConfig {
}
