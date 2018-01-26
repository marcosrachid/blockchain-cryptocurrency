package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;

import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;

public class TransactionOutput {
	public String id;
	public PublicKey reciepient;
	public BigDecimal value;
	public String parentTransactionId;

	public TransactionOutput(PublicKey reciepient, BigDecimal value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = DigestUtil.applySha256(
				TransactionUtil.getStringFromKey(reciepient) + value.setScale(8).toString() + parentTransactionId);
	}

	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
}
