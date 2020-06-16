package com.custom.blockchain.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.transaction.Transaction;

/**
 * 
 * @author marcosrachid
 *
 */
public final class TransactionUtil {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionUtil.class);

	/**
	 * 
	 * @param transactions
	 * @return
	 */
	public static String getMerkleRoot(Collection<Transaction> transactions) {
		LOG.debug("[Crypto] Merkle Root Transactions: {}", transactions);
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
