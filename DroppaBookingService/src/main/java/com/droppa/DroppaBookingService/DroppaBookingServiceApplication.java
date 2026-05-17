package com.droppa.DroppaBookingService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DroppaBookingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DroppaBookingServiceApplication.class, args);
	}

}
