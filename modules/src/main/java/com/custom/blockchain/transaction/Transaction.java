package com.custom.blockchain.transaction;

import static com.custom.blockchain.constants.Properties.MINIMUM_TRANSACTION;
import static com.custom.blockchain.constants.Properties.UNSPENT_TRANSACTIONS_OUTPUT;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;

public class Transaction {
	public String transactionId;
	public PublicKey sender;
	public PublicKey reciepient;
	public BigDecimal value;
	public byte[] signature;

	public List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0;

	public Transaction(PublicKey from, PublicKey to, BigDecimal value, List<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	public void generateSignature(PrivateKey privateKey) {
		String data = TransactionUtil.getStringFromKey(sender) + TransactionUtil.getStringFromKey(reciepient)
				+ value.setScale(8).toString();
		signature = TransactionUtil.applyECDSASig(privateKey, data);
	}

	public boolean verifiySignature() {
		String data = TransactionUtil.getStringFromKey(sender) + TransactionUtil.getStringFromKey(reciepient)
				+ value.setScale(8).toString();
		return TransactionUtil.verifyECDSASig(sender, data, signature);
	}

	public String calulateHash() {
		sequence++;
		return DigestUtil.applySha256(TransactionUtil.getStringFromKey(sender)
				+ TransactionUtil.getStringFromKey(reciepient) + value.setScale(8).toString() + sequence);
	}

	public boolean processTransaction() {

		if (verifiySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		for (TransactionInput i : inputs) {
			i.unspentTransactionOutput = UNSPENT_TRANSACTIONS_OUTPUT.get(i.transactionOutputId);
		}

		if (getInputsValue().compareTo(MINIMUM_TRANSACTION) < 0) {
			System.out.println("#Transaction Inputs too small: " + getInputsValue());
			return false;
		}

		BigDecimal leftOver = getInputsValue().subtract(value);
		transactionId = calulateHash();
		outputs.add(new TransactionOutput(this.reciepient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));

		for (TransactionOutput o : outputs) {
			UNSPENT_TRANSACTIONS_OUTPUT.put(o.id, o);
		}

		for (TransactionInput i : inputs) {
			if (i.unspentTransactionOutput == null)
				continue;
			UNSPENT_TRANSACTIONS_OUTPUT.remove(i.unspentTransactionOutput.id);
		}

		return true;
	}

	public BigDecimal getInputsValue() {
		BigDecimal total = BigDecimal.ZERO;
		for (TransactionInput i : inputs) {
			if (i.unspentTransactionOutput == null)
				continue;
			total = total.add(i.unspentTransactionOutput.value);
		}
		return total;
	}

	public BigDecimal getOutputsValue() {
		BigDecimal total = BigDecimal.ZERO;
		for (TransactionOutput o : outputs) {
			total = total.add(o.value);
		}
		return total;
	}
}
