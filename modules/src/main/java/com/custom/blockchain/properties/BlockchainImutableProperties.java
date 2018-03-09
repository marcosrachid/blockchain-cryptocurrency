package com.custom.blockchain.properties;

import java.util.LinkedList;

import com.custom.blockchain.transaction.Transaction;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockchainImutableProperties {

	public static final Long BLK_DAT_MAX_FILE_SIZE = 134217728L;

	public static final LinkedList<Transaction> TRANSACTION_MEMPOOL = new LinkedList<Transaction>();

	public static final String GENESIS_TX_ID = "0";

	public static final String GENESIS_PREVIOUS_HASH = "0";

}
