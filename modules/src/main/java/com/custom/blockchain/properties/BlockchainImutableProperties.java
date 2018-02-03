package com.custom.blockchain.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.transaction.TransactionOutput;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockchainImutableProperties {

	public static final List<Block> BLOCKCHAIN = new ArrayList<Block>();

	public static final Map<String, TransactionOutput> UNSPENT_TRANSACTIONS_OUTPUT = new HashMap<String, TransactionOutput>();

}
