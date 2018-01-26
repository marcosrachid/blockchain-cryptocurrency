package com.custom.blockchain.transaction;

public class TransactionInput {
	public String transactionOutputId;
	public TransactionOutput unspentTransactionOutput;

	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}
}
