package com.custom.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.custom.blockchain.transaction.component.TransactionMempool;

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
	
	private static final Logger LOG = LoggerFactory.getLogger(Node.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(Node.class, args);
		
		if (TransactionMempool.TRANSACTION_MEMPOOL == null) {
			LOG.error("[Crypto] Transaction mempool was not initiated");
			ctx.close();
		}
	}

}
