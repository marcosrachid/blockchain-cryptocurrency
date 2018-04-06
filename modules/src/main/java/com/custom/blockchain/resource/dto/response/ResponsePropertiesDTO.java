package com.custom.blockchain.resource.dto.response;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.peer.Peer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author marcosrachid
 *
 */
public class ResponsePropertiesDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String coinName;

	private String version;

	private BigDecimal minimunTransaction;

	private BigDecimal coinLimit;

	private BigDecimal miningTimeRate;

	private BigDecimal reward;

	private Long blockSize;

	private Integer networkPort;

	private Integer networkMaximumSeeds;

	private List<Peer> networkMockedPeers;

	private String miner;

	public ResponsePropertiesDTO() {
	}

	public ResponsePropertiesDTO(String coinName, String version, BlockchainProperties blockchainProperties,
			CurrentPropertiesBlockDB currentPropertiesBlockDB)
			throws JsonParseException, JsonMappingException, IOException {
		PropertiesBlock properties = currentPropertiesBlockDB.get();
		this.coinName = coinName;
		this.version = version;
		this.minimunTransaction = properties.getMinimunTransaction();
		this.coinLimit = properties.getCoinLimit();
		this.miningTimeRate = properties.getMiningTimeRate();
		this.reward = properties.getReward();
		this.blockSize = properties.getBlockSize();
		this.networkPort = blockchainProperties.getNetworkPort();
		this.networkMaximumSeeds = blockchainProperties.getNetworkMaximumSeeds();
		this.networkMockedPeers = blockchainProperties.getNetworkMockedPeersMapped();
		this.miner = blockchainProperties.getMiner();
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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

	public Long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(Long blockSize) {
		this.blockSize = blockSize;
	}

	public Integer getNetworkPort() {
		return networkPort;
	}

	public void setNetworkPort(Integer networkPort) {
		this.networkPort = networkPort;
	}

	public Integer getNetworkMaximumSeeds() {
		return networkMaximumSeeds;
	}

	public void setNetworkMaximumSeeds(Integer networkMaximumSeeds) {
		this.networkMaximumSeeds = networkMaximumSeeds;
	}

	public List<Peer> getNetworkMockedPeers() {
		return networkMockedPeers;
	}

	public void setNetworkMockedPeers(List<Peer> networkMockedPeers) {
		this.networkMockedPeers = networkMockedPeers;
	}

	public String getMiner() {
		return miner;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(coinName).append(version).append(minimunTransaction).append(coinLimit)
				.append(miningTimeRate).append(reward).append(blockSize).append(networkPort).append(networkMaximumSeeds)
				.append(networkMockedPeers).append(miner).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponsePropertiesDTO other = (ResponsePropertiesDTO) obj;
		return new EqualsBuilder().append(coinName, other.coinName).append(version, other.version)
				.append(minimunTransaction, other.minimunTransaction).append(coinLimit, other.coinLimit)
				.append(miningTimeRate, other.miningTimeRate).append(reward, other.reward)
				.append(blockSize, other.blockSize).append(networkPort, other.networkPort)
				.append(networkMaximumSeeds, other.networkMaximumSeeds)
				.append(networkMockedPeers, other.networkMockedPeers).append(miner, other.miner).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("coinName", coinName).append("version", version)
				.append("minimunTransaction", minimunTransaction).append("coinLimit", coinLimit)
				.append("miningTimeRate", miningTimeRate).append("reward", reward).append("blockSize", blockSize)
				.append("networkPort", networkPort).append("networkMaximumSeeds", networkMaximumSeeds)
				.append("networkMockedPeers", networkMockedPeers).append("miner", miner).build();
	}

}
