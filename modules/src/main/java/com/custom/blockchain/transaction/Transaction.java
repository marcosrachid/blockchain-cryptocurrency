package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

/**
 * 
 * @author marcosrachid
 *
 */
public abstract class Transaction {

	protected String transactionId;
	protected PublicKey reciepient;
	protected BigDecimal value;

	public static int sequence = 0;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public PublicKey getReciepient() {
		return reciepient;
	}

	public void setReciepient(PublicKey reciepient) {
		this.reciepient = reciepient;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	/**
	 * 
	 * @return
	 */
	public abstract BigDecimal getInputsValue();

	/**
	 * 
	 * @return
	 */
	public abstract BigDecimal getOutputsValue();
}
