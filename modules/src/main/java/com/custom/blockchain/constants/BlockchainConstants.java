package com.custom.blockchain.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.transaction.TransactionOutput;

public class BlockchainConstants {

	public static final String SYSTEM_VERSION = "0.1.0";

	public static final List<Block> BLOCKCHAIN = new ArrayList<Block>();

	public static final Map<String, TransactionOutput> UNSPENT_TRANSACTIONS_OUTPUT = new HashMap<String, TransactionOutput>();

	public static final int DIFFICULTY = 5;

}
