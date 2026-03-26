package com.medis.hospital_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HospitalSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalSystemApplication.class, args);
	}

}
