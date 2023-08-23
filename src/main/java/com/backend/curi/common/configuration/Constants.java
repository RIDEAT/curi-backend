package com.backend.curi.common.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class Constants {
    @Value("${onbird.auth.url}")
    private String AUTH_SERVER;

    @Value("${spring.profiles.active}")
    private String ENV;
}
