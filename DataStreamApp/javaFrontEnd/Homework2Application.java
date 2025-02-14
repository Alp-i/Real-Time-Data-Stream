package com.project.homework2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.project.homework2")
@EnableJpaRepositories(basePackages = "com.project.homework2.Repository")
public class Homework2Application {
	public static void main(String[] args) {
		SpringApplication.run(Homework2Application.class, args);
	}
}
