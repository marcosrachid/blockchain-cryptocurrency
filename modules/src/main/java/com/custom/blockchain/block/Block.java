package com.custom.blockchain.block;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.serializers.PublicKeyDeserializer;
import com.custom.blockchain.serializers.PublicKeySerializer;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.util.DigestUtil;
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

	private boolean genesis = false;
	private long height;
	private String hash;
	private String previousHash;
	private String merkleRoot;
	@JsonSerialize(using = PublicKeySerializer.class)
	@JsonDeserialize(using = PublicKeyDeserializer.class)
	private PublicKey miner;
	private int difficulty;
	private long timeStamp;
	private int nonce;

	private List<Transaction> transactions = new ArrayList<Transaction>();

	public Block() {
		super();
		this.genesis = true;
		this.height = 1L;
		this.timeStamp = new Date().getTime();
		calculateHash();
	}

	public Block(Block previousBlock) {
		super();
		this.height = previousBlock.getHeight() + 1;
		this.previousHash = previousBlock.getHash();
		this.timeStamp = new Date().getTime();
		calculateHash();
	}

	public boolean isGenesis() {
		return genesis;
	}

	public void setGenesis(boolean genesis) {
		this.genesis = genesis;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
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

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getNonce() {
		return nonce;
	}

	public void setNonce(int nonce) {
		this.nonce = nonce;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	/**
	 * 
	 */
	public void calculateHash() {
		hash = DigestUtil
				.applySha256(getPreviousHash() + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(height).append(hash).append(merkleRoot).append(miner).append(difficulty)
				.append(nonce).append(getPreviousHash()).append(timeStamp).append(transactions).hashCode();
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
		return new ToStringBuilder(this).append("blockNumber", height).append("hash", hash)
				.append("merkleRoot", merkleRoot).append("miner", miner).append("difficulty", difficulty)
				.append("nonce", nonce).append("previousHash", getPreviousHash()).append("timeStamp", timeStamp)
				.append("transactions", transactions).build();
	}

}
