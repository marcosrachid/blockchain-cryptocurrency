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
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
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

	private BigDecimal supplyLimit;

	private BigDecimal miningTimeRate;

	private BigDecimal reward;

	private BigDecimal fees;

	private Long blockSize;

	private Integer networkPort;

	private Integer networkMaximumSeeds;

	private List<Peer> networkMockedPeers;

	private String miner;

	public ResponsePropertiesDTO() {
	}

	public ResponsePropertiesDTO(String coinName, String version, BlockchainProperties blockchainProperties,
			CurrentPropertiesChainstateDB currentPropertiesBlockDB)
			throws JsonParseException, JsonMappingException, IOException {
		PropertiesBlock properties = currentPropertiesBlockDB.get();
		this.coinName = coinName;
		this.version = version;
		this.minimunTransaction = properties.getMinimunTransaction();
		this.supplyLimit = properties.getSupplyLimit();
		this.miningTimeRate = properties.getMiningTimeRate();
		this.reward = properties.getReward();
		this.fees = properties.getFees();
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

	public BigDecimal getSupplyLimit() {
		return supplyLimit;
	}

	public void setSupplyLimit(BigDecimal supplyLimit) {
		this.supplyLimit = supplyLimit;
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
		return new HashCodeBuilder().append(coinName).append(version).append(minimunTransaction).append(supplyLimit)
				.append(miningTimeRate).append(reward).append(fees).append(blockSize).append(networkPort)
				.append(networkMaximumSeeds).append(networkMockedPeers).append(miner).hashCode();
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
				.append(minimunTransaction, other.minimunTransaction).append(supplyLimit, other.supplyLimit)
				.append(miningTimeRate, other.miningTimeRate).append(reward, other.reward).append(fees, other.fees)
				.append(blockSize, other.blockSize).append(networkPort, other.networkPort)
				.append(networkMaximumSeeds, other.networkMaximumSeeds)
				.append(networkMockedPeers, other.networkMockedPeers).append(miner, other.miner).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("coinName", coinName).append("version", version)
				.append("minimunTransaction", minimunTransaction).append("coinLimit", supplyLimit)
				.append("miningTimeRate", miningTimeRate).append("reward", reward).append("fees", fees)
				.append("blockSize", blockSize).append("networkPort", networkPort)
				.append("networkMaximumSeeds", networkMaximumSeeds).append("networkMockedPeers", networkMockedPeers)
				.append("miner", miner).build();
	}

}
