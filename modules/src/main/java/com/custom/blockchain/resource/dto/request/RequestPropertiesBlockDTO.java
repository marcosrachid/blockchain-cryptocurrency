package com.custom.blockchain.resource.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RequestPropertiesBlockDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal minimunTransaction;

	private BigDecimal coinLimit;

	private BigDecimal miningTimeRate;

	private BigDecimal reward;

	private BigDecimal fees;

	private Long blockSize;

	private String coinbase;

	private BigDecimal premined;

	private Integer startingDifficulty;

	public RequestPropertiesBlockDTO() {
	}

	public BigDecimal getMinimunTransaction() {
		return minimunTransaction;
	}

	public void setMinimunTransaction(BigDecimal minimunTransaction) {
		this.minimunTransaction = minimunTransaction;
	}

	public BigDecimal getCoinLimit() {
		return coinLimit;
	}

	public void setCoinLimit(BigDecimal coinLimit) {
		this.coinLimit = coinLimit;
	}

	public BigDecimal getMiningTimeRate() {
		return miningTimeRate;
	}

	public void setMiningTimeRate(BigDecimal miningTimeRate) {
		this.miningTimeRate = miningTimeRate;
	}

	public BigDecimal getReward() {
		return reward;
	}

	public void setReward(BigDecimal reward) {
		this.reward = reward;
	}

	public BigDecimal getFees() {
		return fees;
	}

	public void setFees(BigDecimal fees) {
		this.fees = fees;
	}

	public Long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(Long blockSize) {
		this.blockSize = blockSize;
	}

	public String getCoinbase() {
		return coinbase;
	}

	public void setCoinbase(String coinbase) {
		this.coinbase = coinbase;
	}

	public BigDecimal getPremined() {
		return premined;
	}

	public void setPremined(BigDecimal premined) {
		this.premined = premined;
	}

	public Integer getStartingDifficulty() {
		return startingDifficulty;
	}

	public void setStartingDifficulty(Integer startingDifficulty) {
		this.startingDifficulty = startingDifficulty;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(minimunTransaction).append(coinLimit).append(miningTimeRate).append(reward)
				.append(fees).append(blockSize).append(coinbase).append(premined).append(startingDifficulty).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestPropertiesBlockDTO other = (RequestPropertiesBlockDTO) obj;
		return new EqualsBuilder().append(minimunTransaction, other.minimunTransaction)
				.append(coinLimit, other.coinLimit).append(miningTimeRate, other.miningTimeRate)
				.append(reward, other.reward).append(fees, other.fees).append(blockSize, other.blockSize)
				.append(coinbase, other.coinbase).append(premined, other.premined)
				.append(startingDifficulty, other.startingDifficulty).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("minimunTransaction", minimunTransaction).append("coinLimit", coinLimit)
				.append("miningTimeRate", miningTimeRate).append("reward", reward).append("fees", fees)
				.append("blockSize", blockSize).append("coinbase", coinbase).append("premined", premined)
				.append("startingDifficulty", startingDifficulty).build();
	}

}
