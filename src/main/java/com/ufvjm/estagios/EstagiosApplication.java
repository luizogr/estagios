package com.ufvjm.estagios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EstagiosApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstagiosApplication.class, args);
	}

}
