package com.nelani.blog_land_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.nelani.blog_land_backend.repository")
public class BlogLandBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogLandBackendApplication.class, args);
	}

}
