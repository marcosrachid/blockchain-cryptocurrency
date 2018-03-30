package com.custom.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 
 * @author marcosrachid
 *
 */
@SpringBootApplication
@ComponentScan("com.custom.blockchain")
@PropertySources(value = { @PropertySource("classpath:config/application.properties") })
@EnableScheduling
public class Node {

	public static void main(String[] args) {
		SpringApplication.run(Node.class, args);
	}

}
