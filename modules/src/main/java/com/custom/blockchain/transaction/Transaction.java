package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactionId).append(reciepient).append(value).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleTransaction other = (SimpleTransaction) obj;
		return new EqualsBuilder().append(transactionId, other.transactionId).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("transactionId", transactionId).append("reciepient", reciepient)
				.append("value", value).build();
	}

}
