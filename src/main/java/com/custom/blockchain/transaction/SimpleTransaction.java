package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.serializers.PublicKeyDeserializer;
import com.custom.blockchain.serializers.PublicKeySerializer;
import com.custom.blockchain.util.WalletUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author marcosrachid
 *
 */
public class SimpleTransaction extends Transaction {

	private static final long serialVersionUID = 1L;

	@JsonSerialize(using = PublicKeySerializer.class)
	@JsonDeserialize(using = PublicKeyDeserializer.class)
	private PublicKey sender;
	private BigDecimal feeValue;
	private byte[] signature;

	private List<TransactionInput> inputs = new ArrayList<>();
	private List<TransactionOutput> outputs = new ArrayList<>();

	public SimpleTransaction() {
	}

	public SimpleTransaction(PublicKey from, BigDecimal value) {
		this.sender = from;
		this.value = value;
		this.timeStamp = new Date().getTime();
	}

	public PublicKey getSender() {
		return sender;
	}

	public void setSender(PublicKey sender) {
		this.sender = sender;
	}

	public BigDecimal getFeeValue() {
		return feeValue;
	}

	public void setFeeValue(BigDecimal feeValue) {
		this.feeValue = feeValue;
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
	 * @param fees
	 */
	@Override
	public void applyFees(BigDecimal fees) {
		this.feeValue = this.value.multiply(fees);
		this.outputs.forEach(o -> {
			BigDecimal feeValue = o.getValue().multiply(fees);
			o.subtractFee(feeValue);
		});
	}

	/**
	 * 
	 * @return
	 */
	@Override
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
	@Override
	public BigDecimal getOutputsValue() {
		BigDecimal total = BigDecimal.ZERO;
		for (TransactionOutput o : outputs) {
			total = total.add(o.getValue());
		}
		return total;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactionId).append(sender).append(value).append(feeValue)
				.append(timeStamp).append(signature).append(inputs).append(outputs).hashCode();
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
		return new ToStringBuilder(this).append("transactionId", transactionId)
				.append("sender", WalletUtil.getStringFromKey(sender)).append("value", value)
				.append("feeValue", feeValue).append("timeStamp", timeStamp).append("signature", signature)
				.append("inputs", inputs).append("outputs", outputs).build();
	}

}
