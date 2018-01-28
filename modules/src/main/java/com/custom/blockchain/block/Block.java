package com.custom.blockchain.block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.util.DigestUtil;

public class Block implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String hash;
	protected String previousHash;
	protected String merkleRoot;
	protected List<Transaction> transactions = new ArrayList<Transaction>();
	protected long timeStamp;
	protected int nonce;

	public Block(String previousHash) {
		super();
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		calculateHash();
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
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

	public void calculateHash() {
		hash = DigestUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(hash).append(merkleRoot).append(nonce).append(previousHash)
				.append(timeStamp).append(transactions).hashCode();
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
		return new EqualsBuilder().append(hash, other.hash).append(merkleRoot, other.merkleRoot)
				.append(nonce, other.nonce).append(previousHash, other.previousHash).append(timeStamp, other.timeStamp)
				.append(transactions, other.transactions).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("hash", hash).append("merkleRoot", merkleRoot).append("nonce", nonce)
				.append("previousHash", previousHash).append("timeStamp", timeStamp)
				.append("transactions", transactions).build();
	}

}
