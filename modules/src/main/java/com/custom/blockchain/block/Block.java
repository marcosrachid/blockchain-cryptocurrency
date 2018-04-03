package com.custom.blockchain.block;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.serializers.PublicKeyDeserializer;
import com.custom.blockchain.serializers.PublicKeySerializer;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.WalletUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 
 * @author marcosrachid
 *
 */
public class Block implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String GENESIS_PREVIOUS_HASH = "0";

	private Boolean genesis = false;
	private Long height;
	private String hash;
	private String previousHash;
	private String merkleRoot;
	@JsonSerialize(using = PublicKeySerializer.class)
	@JsonDeserialize(using = PublicKeyDeserializer.class)
	private PublicKey miner;
	private Long timeStamp;
	private Integer nonce;

	private Set<Transaction> transactions = new HashSet<>();

	public Block() {
		super();
		this.genesis = true;
		this.height = 1L;
		this.timeStamp = new Date().getTime();
		this.nonce = 0;
		calculateHash();
	}

	public Block(Block previousBlock) {
		super();
		this.height = previousBlock.getHeight() + 1;
		this.previousHash = previousBlock.getHash();
		this.timeStamp = new Date().getTime();
		this.nonce = 0;
		calculateHash();
	}

	public Boolean isGenesis() {
		return genesis;
	}

	public void setGenesis(Boolean genesis) {
		this.genesis = genesis;
	}

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public String getPreviousHash() {
		return (previousHash != null) ? previousHash : GENESIS_PREVIOUS_HASH;
	}

	public String getMerkleRoot() {
		return merkleRoot;
	}

	public void setMerkleRoot(String merkleRoot) {
		this.merkleRoot = merkleRoot;
	}

	public PublicKey getMiner() {
		return miner;
	}

	public void setMiner(PublicKey miner) {
		this.miner = miner;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Integer getNonce() {
		return nonce;
	}

	public void setNonce(Integer nonce) {
		this.nonce = nonce;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	@JsonIgnore
	public RewardTransaction getRewardTransaction() {
		Optional<Transaction> transaction = getTransactions().stream().filter(t -> t instanceof RewardTransaction)
				.findFirst();
		if (transaction.isPresent())
			return (RewardTransaction) transaction.get();
		return null;
	}

	/**
	 * 
	 */
	public void calculateHash() {
		hash = DigestUtil.applySha256(getPreviousHash() + timeStamp + nonce + merkleRoot);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(height).append(hash).append(merkleRoot).append(miner).append(nonce)
				.append(getPreviousHash()).append(timeStamp).append(transactions).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		return new EqualsBuilder().append(hash, other.hash).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("height", height).append("hash", hash).append("merkleRoot", merkleRoot)
				.append("miner", WalletUtil.getStringFromKey(miner)).append("nonce", nonce)
				.append("previousHash", getPreviousHash()).append("timeStamp", timeStamp)
				.append("transactions", transactions).build();
	}

}
