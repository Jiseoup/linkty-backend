package com.linkty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LinktyApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinktyApplication.class, args);
	}

}
