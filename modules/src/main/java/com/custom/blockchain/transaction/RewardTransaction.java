package com.custom.blockchain.transaction;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.util.DigestUtil;

public class RewardTransaction extends Transaction {

	private String coinbase;

	private TransactionOutput output;

	private Integer difficulty;

	public RewardTransaction() {
	}

	public RewardTransaction(String coinbase, BigDecimal value, Integer difficulty) {
		this.coinbase = coinbase;
		this.value = value;
		this.difficulty = difficulty;
		this.timeStamp = new Date().getTime();
		generateCoinbase();
	}

	public RewardTransaction(BigDecimal value) {
		generateCoinbase();
		this.value = value;
	}

	public String getCoinbase() {
		return coinbase;
	}

	public void setCoinbase(String coinbase) {
		this.coinbase = coinbase;
	}

	public TransactionOutput getOutput() {
		return output;
	}

	public void setOutput(TransactionOutput output) {
		this.output = output;
	}

	public Integer getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Integer difficulty) {
		this.difficulty = difficulty;
	}

	@Override
	public BigDecimal getInputsValue() {
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getOutputsValue() {
		return output.getValue();
	}

	private void generateCoinbase() {
		Transaction.sequence++;
		coinbase = DigestUtil
				.applySha256(DigestUtil.applySha256(difficulty + timeStamp + Transaction.sequence + coinbase));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactionId).append(value).append(timeStamp).append(coinbase)
				.append(output).hashCode();
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
		return new ToStringBuilder(this).append("transactionId", transactionId).append("value", value)
				.append("timeStamp", timeStamp).append("coinbase", coinbase).append("output", output).build();
	}

}
