package com.custom.blockchain.transaction;

import java.security.PublicKey;

import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;

public class TransactionOutput {
	public String id;
	public PublicKey reciepient;
	public float value;
	public String parentTransactionId;

	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = DigestUtil.applySha256(
				TransactionUtil.getStringFromKey(reciepient) + Float.toString(value) + parentTransactionId);
	}

	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
}
