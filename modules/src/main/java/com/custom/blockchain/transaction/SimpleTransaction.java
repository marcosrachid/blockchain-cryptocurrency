package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class SimpleTransaction extends Transaction {

	private PublicKey sender;
	private byte[] signature;

	private List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	private List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	public SimpleTransaction(PublicKey from, PublicKey to, BigDecimal value, List<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	public PublicKey getSender() {
		return sender;
	}

	public void setSender(PublicKey sender) {
		this.sender = sender;
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

	/**
	 * 
	 * @return
	 */
	public BigDecimal getInputsValue() {
		BigDecimal total = BigDecimal.ZERO;
		for (TransactionInput i : inputs) {
			if (i.getUnspentTransactionOutput() == null)
				continue;
			total = total.add(i.getUnspentTransactionOutput().getValue());
		}
		return total;
	}

	/**
	 * 
	 * @return
	 */
	public BigDecimal getOutputsValue() {
		BigDecimal total = BigDecimal.ZERO;
		for (TransactionOutput o : outputs) {
			total = total.add(o.getValue());
		}
		return total;
	}

}
