package com.greivin.txapi;

import org.springframework.boot.SpringApplication;

public class TestTransactionProcessingApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TransactionProcessingApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
