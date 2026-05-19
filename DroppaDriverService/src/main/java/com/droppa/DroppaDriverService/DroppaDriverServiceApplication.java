package com.droppa.DroppaDriverService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DroppaDriverServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DroppaDriverServiceApplication.class, args);
	}

}
