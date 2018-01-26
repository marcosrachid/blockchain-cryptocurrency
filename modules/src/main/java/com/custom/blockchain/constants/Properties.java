package com.custom.blockchain.constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.transaction.TransactionOutput;

public class Properties {
	
	public static final List<Block> BLOCKCHAIN = new ArrayList<Block>();
	
	public static final Map<String, TransactionOutput> UNSPENT_TRANSACTIONS_OUTPUT = new HashMap<String, TransactionOutput>();

	public static final int DIFFICULTY = 5;
	
	public static final BigDecimal MINIMUM_TRANSACTION = new BigDecimal(0.00000001f);
	
	public static final String GENESIS_TRANSACTION_ID = "0";
	
}
