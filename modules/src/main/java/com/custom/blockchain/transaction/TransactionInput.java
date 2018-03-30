package com.custom.blockchain.transaction;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author marcosrachid
 *
 */
public class TransactionInput {

	private String transactionOutputId;
	private TransactionOutput unspentTransactionOutput;

	public TransactionInput(TransactionOutput u) {
		this.transactionOutputId = u.getId();
		this.unspentTransactionOutput = u;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactionOutputId).append(unspentTransactionOutput).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionInput other = (TransactionInput) obj;
		return new EqualsBuilder().append(transactionOutputId, other.transactionOutputId)
				.append(unspentTransactionOutput, other.unspentTransactionOutput).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("transactionOutputId", transactionOutputId)
				.append("unspentTransactionOutput", unspentTransactionOutput).build();
	}

}
