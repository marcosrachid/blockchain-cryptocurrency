package com.custom.blockchain.transaction;

/**
 * 
 * @author marcosrachid
 *
 */
public class TransactionInput {
	private String transactionOutputId;
	private TransactionOutput unspentTransactionOutput;

	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public String getTransactionOutputId() {
		return transactionOutputId;
	}

	public void setTransactionOutputId(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public TransactionOutput getUnspentTransactionOutput() {
		return unspentTransactionOutput;
	}

	public void setUnspentTransactionOutput(TransactionOutput unspentTransactionOutput) {
		this.unspentTransactionOutput = unspentTransactionOutput;
	}
}
