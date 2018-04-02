package com.custom.blockchain.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.PublicKey;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.serializers.PublicKeyDeserializer;
import com.custom.blockchain.serializers.PublicKeySerializer;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.WalletUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author marcosrachid
 *
 */
public class TransactionOutput implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	@JsonSerialize(using = PublicKeySerializer.class)
	@JsonDeserialize(using = PublicKeyDeserializer.class)
	private PublicKey reciepient;
	private BigDecimal value;
	private String parentTransactionId;

	public TransactionOutput() {
	}

	public TransactionOutput(String id, PublicKey reciepient, BigDecimal value, String parentTransactionId) {
		this.id = id;
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
	}

	public TransactionOutput(PublicKey reciepient, BigDecimal value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = DigestUtil.applySha256(
				WalletUtil.getStringFromKey(reciepient) + value.setScale(8).toString() + parentTransactionId);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getParentTransactionId() {
		return parentTransactionId;
	}

	public void setParentTransactionId(String parentTransactionId) {
		this.parentTransactionId = parentTransactionId;
	}

	public boolean isMine(PublicKey publicKey) {
		return (publicKey.equals(reciepient));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(reciepient).append(value).append(parentTransactionId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionOutput other = (TransactionOutput) obj;
		return new EqualsBuilder().append(id, other.id).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id).append("reciepient", WalletUtil.getStringFromKey(reciepient))
				.append("value", value).append("parentTransactionId", parentTransactionId).build();
	}

}
