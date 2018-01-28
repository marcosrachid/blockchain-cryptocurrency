package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
	private String transactionId;
	private PublicKey sender;
	private PublicKey reciepient;
	private BigDecimal value;
	private byte[] signature;

	private List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	private List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	public static int sequence = 0;

	public Transaction(PublicKey from, PublicKey to, BigDecimal value, List<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public PublicKey getSender() {
		return sender;
	}

	public void setSender(PublicKey sender) {
		this.sender = sender;
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

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public List<TransactionInput> getInputs() {
		return inputs;
	}

	public void setInputs(List<TransactionInput> inputs) {
		this.inputs = inputs;
	}

	public List<TransactionOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<TransactionOutput> outputs) {
		this.outputs = outputs;
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
