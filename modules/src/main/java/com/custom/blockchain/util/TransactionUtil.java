package com.custom.blockchain.util;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKED;

import java.util.ArrayList;
import java.util.List;

import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.exception.TransactionException;

/**
 * 
 * @author marcosrachid
 *
 */
public class TransactionUtil {

	/**
	 * 
	 */
	public static void blockTransactions() {
		BLOCKED = true;
	}

	/**
	 * 
	 */
	public static void unblockTransactions() {
		BLOCKED = false;
	}

	/**
	 * 
	 * @return
	 * @throws TransactionException
	 */
	public static void checkTransactionBlocked() throws TransactionException {
		if (BLOCKED) {
			throw new TransactionException("Transactions are currenctly blocked. Node is syncing");
		}
	}

	/**
	 * 
	 * @param transactions
	 * @return
	 */
	public static String getMerkleRoot(List<Transaction> transactions) {
		int count = transactions.size();
		List<String> previousTreeLayer = new ArrayList<String>();
		for (Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.getTransactionId());
		}
		List<String> treeLayer = previousTreeLayer;
		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(DigestUtil.applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

}
