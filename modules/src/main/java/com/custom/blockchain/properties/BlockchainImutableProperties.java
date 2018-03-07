package com.custom.blockchain.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockchainImutableProperties {

	public static final Long BLK_DAT_MAX_FILE_SIZE = 134217728L;

	public static final LinkedList<Transaction> TRANSACTION_MEMPOOL = new LinkedList<Transaction>();

	// TODO: will be removed
	public static final List<Block> BLOCKCHAIN = new ArrayList<Block>();

	// TODO: will be removed
	public static final Map<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

}
