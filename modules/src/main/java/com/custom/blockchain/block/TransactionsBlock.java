package com.custom.blockchain.block;

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
public class TransactionsBlock extends AbstractBlock {

	private static final long serialVersionUID = 1L;

	private String merkleRoot;
	@JsonSerialize(using = PublicKeySerializer.class)
	@JsonDeserialize(using = PublicKeyDeserializer.class)
	private PublicKey miner;
	private Integer nonce;
	private String propertiesHash;

	private Set<Transaction> transactions = new HashSet<>();

	public TransactionsBlock() {
	}

	public TransactionsBlock(AbstractBlock previousBlock, PropertiesBlock propertiesBlock) {
		super();
		this.height = previousBlock.getHeight() + 1;
		this.previousHash = previousBlock.getHash();
		this.propertiesHash = propertiesBlock.getHash();
		this.timeStamp = new Date().getTime();
		this.nonce = 0;
		calculateHash();
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

	public Integer getNonce() {
		return nonce;
	}

	public void setNonce(Integer nonce) {
		this.nonce = nonce;
	}

	public String getPropertiesHash() {
		return propertiesHash;
	}

	public void setPropertiesHash(String propertiesHash) {
		this.propertiesHash = propertiesHash;
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
	@Override
	public void calculateHash() {
		hash = DigestUtil.applySha256(previousHash + propertiesHash + timeStamp + nonce + merkleRoot);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(height).append(hash).append(merkleRoot).append(miner).append(nonce)
				.append(previousHash).append(timeStamp).append(transactions).hashCode();
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
		return new ToStringBuilder(this).append("height", height).append("hash", hash).append("merkleRoot", merkleRoot)
				.append("miner", WalletUtil.getStringFromKey(miner)).append("nonce", nonce)
				.append("previousHash", previousHash).append("timeStamp", timeStamp)
				.append("transactions", transactions).build();
	}

}
