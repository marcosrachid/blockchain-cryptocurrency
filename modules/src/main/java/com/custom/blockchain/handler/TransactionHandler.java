package com.custom.blockchain.handler;

import org.springframework.stereotype.Component;

import com.custom.blockchain.service.TransactionService;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class TransactionHandler {

	private TransactionService transactionService;

	public TransactionHandler(final TransactionService transactionService) {
		this.transactionService = transactionService;
	}
}
