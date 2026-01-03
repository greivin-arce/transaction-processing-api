package com.greivin.txapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.greivin.txapi.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class TransactionProcessingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionProcessingApiApplication.class, args);
	}

}
