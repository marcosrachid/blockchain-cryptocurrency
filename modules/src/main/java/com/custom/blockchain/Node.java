package com.custom.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@ComponentScan("com.custom.blockchain")
@PropertySources(value = { @PropertySource("classpath:config/application.properties") })
public class Node {

	public static void main(String[] args) {
		SpringApplication.run(Node.class, args);
	}

}
