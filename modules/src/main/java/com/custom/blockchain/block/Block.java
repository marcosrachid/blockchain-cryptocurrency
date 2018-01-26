package com.custom.blockchain.block;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;

public class Block {

	public String hash;
	public String previousHash;
	public String merkleRoot;
	public List<Transaction> transactions = new ArrayList<Transaction>();
	public long timeStamp;
	public int nonce;

	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}

	public String calculateHash() {
		String calculatedhash = DigestUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedhash;
	}

	public void mineBlock(int difficulty) {
		merkleRoot = TransactionUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty);
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}

	public boolean addTransaction(Transaction transaction) {
		if (transaction == null)
			return false;
		if ((previousHash != "0")) {
			if ((transaction.processTransaction() != true)) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}

}
