package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author marcosrachid
 *
 */
public class ResponseTransaction implements Serializable {

	private static final long serialVersionUID = 1L;

	private String transactionId;
	private String senderPublicKey;
	private String reciepientPublicKey;
	private BigDecimal value;

	public ResponseTransaction(String transactionId, String senderPublicKey, String reciepientPublicKey,
			BigDecimal value) {
		super();
		this.transactionId = transactionId;
		this.senderPublicKey = senderPublicKey;
		this.reciepientPublicKey = reciepientPublicKey;
		this.value = value;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public String getReciepientPublicKey() {
		return reciepientPublicKey;
	}

	public void setReciepientPublicKey(String reciepientPublicKey) {
		this.reciepientPublicKey = reciepientPublicKey;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactionId).append(senderPublicKey).append(reciepientPublicKey)
				.append(value).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseTransaction other = (ResponseTransaction) obj;
		return new EqualsBuilder().append(transactionId, other.transactionId)
				.append(senderPublicKey, other.senderPublicKey).append(reciepientPublicKey, other.reciepientPublicKey)
				.append(value, other.value).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("transactionId", transactionId)
				.append("senderPublicKey", senderPublicKey).append("reciepientPublicKey", reciepientPublicKey)
				.append("value", value).build();
	}

}
