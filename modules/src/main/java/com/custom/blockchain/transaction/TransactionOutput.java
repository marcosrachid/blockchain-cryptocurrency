package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;

public class TransactionOutput {
	private String id;
	private PublicKey reciepient;
	private BigDecimal value;
	private String parentTransactionId;

	public TransactionOutput(PublicKey reciepient, BigDecimal value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = DigestUtil.applySha256(
				TransactionUtil.getStringFromKey(reciepient) + value.setScale(8).toString() + parentTransactionId);
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
		return (publicKey == reciepient);
	}
}
