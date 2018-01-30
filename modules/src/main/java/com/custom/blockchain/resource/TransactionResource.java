package com.custom.blockchain.resource;

import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.handler.TransactionHandler;

@RestController
public class TransactionResource {

	private TransactionHandler transactionHandler;

	public TransactionResource(final TransactionHandler transactionHandler) {
		this.transactionHandler = transactionHandler;
	}

}
