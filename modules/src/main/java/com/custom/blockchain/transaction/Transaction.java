package com.custom.blockchain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.custom.blockchain.Node;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;

public class Transaction {
	public String transactionId;
	public PublicKey sender;
	public PublicKey reciepient;
	public float value;
	public byte[] signature;

	public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0;

	public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
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

	public String calulateHash() {
		sequence++;
		return DigestUtil.applySha256(TransactionUtil.getStringFromKey(sender)
				+ TransactionUtil.getStringFromKey(reciepient) + Float.toString(value) + sequence);
	}

	public boolean processTransaction() {

		if (verifiySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		for (TransactionInput i : inputs) {
			i.unspentTransactionOutput = Node.UTXOs.get(i.transactionOutputId);
		}

		if (getInputsValue() < Node.minimumTransaction) {
			System.out.println("#Transaction Inputs too small: " + getInputsValue());
			return false;
		}

		float leftOver = getInputsValue() - value;
		transactionId = calulateHash();
		outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

		for (TransactionOutput o : outputs) {
			Node.UTXOs.put(o.id, o);
		}

		for (TransactionInput i : inputs) {
			if (i.unspentTransactionOutput == null)
				continue;
			Node.UTXOs.remove(i.unspentTransactionOutput.id);
		}

		return true;
	}

	public float getInputsValue() {
		float total = 0;
		for (TransactionInput i : inputs) {
			if (i.unspentTransactionOutput == null)
				continue;
			total += i.unspentTransactionOutput.value;
		}
		return total;
	}

	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}
