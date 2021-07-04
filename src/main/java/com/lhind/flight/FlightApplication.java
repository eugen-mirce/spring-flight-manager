package com.lhind.flight;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FlightApplication {
	static final Logger logger = Logger.getLogger(FlightApplication.class);

	public static void main(String[] args) {
		logger.info("Before Starting application.");
		SpringApplication.run(FlightApplication.class, args);
		logger.debug("Starting my application in debug mode.");
		logger.info("Starting my application.");
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}
}
