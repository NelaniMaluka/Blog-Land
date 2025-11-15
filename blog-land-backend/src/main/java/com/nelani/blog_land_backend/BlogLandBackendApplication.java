package com.nelani.blog_land_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BlogLandBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogLandBackendApplication.class, args);
	}

}
