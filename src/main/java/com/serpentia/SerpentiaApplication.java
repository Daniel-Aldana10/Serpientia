package com.serpentia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SerpentiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerpentiaApplication.class, args);
	}

}
