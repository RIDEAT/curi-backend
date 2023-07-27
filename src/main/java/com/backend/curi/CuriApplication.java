package com.backend.curi;

import com.backend.curi.workflow.repository.ContentRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = ContentRepository.class)
@EnableJpaRepositories(excludeFilters =
	@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ContentRepository.class))
@OpenAPIDefinition(servers = {@Server(url="/", description = "Default Server Url")})
@SpringBootApplication
public class CuriApplication {

	private static Logger log = LoggerFactory.getLogger(CuriApplication.class);

	public static void main(String[] args) {

		log.info("Hello slf4j");
		log.info("Hello json log");

		log.error("This is error");

		log.warn("this is warn");

		SpringApplication.run(CuriApplication.class, args);
	}

}
