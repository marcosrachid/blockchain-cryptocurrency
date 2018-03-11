package com.custom.blockchain.block;

import static com.custom.blockchain.properties.BlockchainImutableProperties.GENESIS_PREVIOUS_HASH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.util.DigestUtil;

/**
 * 
 * @author marcosrachid
 *
 */
public class Block implements Serializable {

	private static final long serialVersionUID = 3050609047223755104L;

	private boolean genesis = false;
	private long blockNumber;
	private String hash;
	private Block previousBlock;
	private String merkleRoot;
	private List<Transaction> transactions = new ArrayList<Transaction>();
	private long timeStamp;
	private int nonce;

	public Block() {
		super();
		this.genesis = true;
		this.blockNumber = 1L;
		this.timeStamp = new Date().getTime();
		calculateHash();
	}

	public Block(Block previousBlock) {
		super();
		this.blockNumber = previousBlock.getBlockNumber() + 1;
		this.previousBlock = previousBlock;
		this.timeStamp = new Date().getTime();
		calculateHash();
	}

	public boolean isGenesis() {
		return genesis;
	}

	public void setGenesis(boolean genesis) {
		this.genesis = genesis;
	}

	public long getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(long blockNumber) {
		this.blockNumber = blockNumber;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Block getPreviousBlock() {
		return previousBlock;
	}

	public void setPreviousBlock(Block previousBlock) {
		this.previousBlock = previousBlock;
	}

	public String getPreviousHash() {
		return (previousBlock != null) ? previousBlock.getHash() : GENESIS_PREVIOUS_HASH;
	}

	public String getMerkleRoot() {
		return merkleRoot;
	}

	public void setMerkleRoot(String merkleRoot) {
		this.merkleRoot = merkleRoot;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
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

	/**
	 * 
	 */
	public void calculateHash() {
		hash = DigestUtil
				.applySha256(getPreviousHash() + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(blockNumber).append(hash).append(merkleRoot).append(nonce)
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
		return new ToStringBuilder(this).append("blockNumber", blockNumber).append("hash", hash)
				.append("merkleRoot", merkleRoot).append("nonce", nonce).append("previousHash", getPreviousHash())
				.append("timeStamp", timeStamp).append("transactions", transactions).build();
	}

}
