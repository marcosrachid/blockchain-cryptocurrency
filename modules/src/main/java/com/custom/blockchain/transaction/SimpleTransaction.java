package com.custom.blockchain.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SimpleTransaction extends Transaction implements Serializable {

	private static final long serialVersionUID = 7034357259560766328L;

	private PublicKey sender;
	private byte[] signature;

	private List<TransactionInput> inputs = new ArrayList<>();
	private List<TransactionOutput> outputs = new ArrayList<>();

	public SimpleTransaction(PublicKey from, BigDecimal value, List<TransactionInput> inputs) {
		this.sender = from;
		this.value = value;
		this.inputs = inputs;
		this.timeStamp = new Date().getTime();
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

	public List<TransactionOutput.TransactionOutputSerializable> getSerializableOutputs() {
		List<TransactionOutput.TransactionOutputSerializable> serializableList = new ArrayList<>();
		for (TransactionOutput utxo : outputs) {
			serializableList.add(utxo.serializable());
		}
		return serializableList;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactionId).append(sender).append(value).append(timeStamp)
				.append(signature).append(inputs).append(outputs).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleTransaction other = (SimpleTransaction) obj;
		return new EqualsBuilder().append(transactionId, other.transactionId).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("transactionId", transactionId).append("sender", sender)
				.append("value", value).append("timeStamp", timeStamp).append("signature", signature)
				.append("inputs", inputs).append("outputs", outputs).build();
	}

}
