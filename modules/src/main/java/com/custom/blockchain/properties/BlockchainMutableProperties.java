package com.custom.blockchain.properties;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.wallet.Wallet;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockchainMutableProperties {

	public static Integer DIFFICULTY = 5;
	
	public static boolean BLOCKED = false;
	
	public static Block GENESIS_BLOCK;
	
	public static Block PREVIOUS_BLOCK;
	
	public static Block CURRENT_BLOCK;
	
	public static Wallet CURRENT_WALLET;

}
