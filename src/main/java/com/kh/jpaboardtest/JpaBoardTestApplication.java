package com.kh.jpaboardtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JpaBoardTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaBoardTestApplication.class, args);
	}

}
