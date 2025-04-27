package com.devops.devops_accommodation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DevopsAccommodationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsAccommodationApplication.class, args);
	}

}
