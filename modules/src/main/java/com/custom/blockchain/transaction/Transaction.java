package com.custom.blockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;

public class Transaction {
	public String transactionId;
	public PublicKey sender;
	public PublicKey reciepient;
	public float value;
	public byte[] signature;

	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0;

	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	public void generateSignature(PrivateKey privateKey) {
		String data = TransactionUtil.getStringFromKey(sender) + TransactionUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		signature = TransactionUtil.applyECDSASig(privateKey, data);
	}

	public boolean verifiySignature() {
		String data = TransactionUtil.getStringFromKey(sender) + TransactionUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		return TransactionUtil.verifyECDSASig(sender, data, signature);
	}

	private String calulateHash() {
		sequence++;
		return DigestUtil.applySha256(TransactionUtil.getStringFromKey(sender)
				+ TransactionUtil.getStringFromKey(reciepient) + Float.toString(value) + sequence);
	}
}
