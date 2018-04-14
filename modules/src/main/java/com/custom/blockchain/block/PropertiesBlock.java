package com.custom.blockchain.block;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.util.DigestUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PropertiesBlock extends AbstractBlock {

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

	public PropertiesBlock() {
		this.previousHash = "0";
		this.height = 1L;
		this.timeStamp = System.currentTimeMillis();
	}

	public PropertiesBlock(BigDecimal minimunTransaction, BigDecimal coinLimit, BigDecimal miningTimeRate,
			BigDecimal reward, BigDecimal fees, Long blockSize, String coinbase) {
		this.minimunTransaction = minimunTransaction;
		this.coinLimit = coinLimit;
		this.miningTimeRate = miningTimeRate;
		this.reward = reward;
		this.fees = fees;
		this.blockSize = blockSize;
		this.coinbase = coinbase;
		this.previousHash = "0";
		this.height = 1L;
		this.timeStamp = System.currentTimeMillis();
	}

	public PropertiesBlock(BigDecimal minimunTransaction, BigDecimal coinLimit, BigDecimal miningTimeRate,
			BigDecimal reward, BigDecimal fees, Long blockSize, String coinbase, AbstractBlock previousBlock) {
		this.minimunTransaction = minimunTransaction;
		this.coinLimit = coinLimit;
		this.miningTimeRate = miningTimeRate;
		this.reward = reward;
		this.fees = fees;
		this.blockSize = blockSize;
		this.coinbase = coinbase;
		this.previousHash = previousBlock.getHash();
		this.height = previousBlock.getHeight() + 1;
		this.timeStamp = System.currentTimeMillis();
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

	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public String getNetworkSignature() {
		return DigestUtil
				.applySha256(DigestUtil.applySha256(minimunTransaction.toPlainString() + coinLimit.toPlainString()
						+ miningTimeRate + reward.toPlainString() + fees.toPlainString() + blockSize + coinbase));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(height).append(hash).append(previousHash).append(timeStamp)
				.append(minimunTransaction).append(coinLimit).append(miningTimeRate).append(reward).append(fees)
				.append(blockSize).append(coinbase).append(premined).append(startingDifficulty).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionsBlock other = (TransactionsBlock) obj;
		return new EqualsBuilder().append(hash, other.hash).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("height", height).append("hash", hash)
				.append("previousHash", previousHash).append("timeStamp", timeStamp)
				.append("minimunTransaction", minimunTransaction).append("coinLimit", coinLimit)
				.append("miningTimeRate", miningTimeRate).append("reward", reward).append("fees", fees)
				.append("blockSize", blockSize).append("coinbase", coinbase).append("premined", premined)
				.append("startingDifficulty", startingDifficulty).build();
	}

}
