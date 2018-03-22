package com.custom.blockchain.block;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockStateManagement {

	public static Block GENESIS_BLOCK;

	public static Block PREVIOUS_BLOCK;

	public static Block CURRENT_BLOCK;
	
	public static void foundBlock() {
		PREVIOUS_BLOCK = CURRENT_BLOCK;
		CURRENT_BLOCK = BlockFactory.getBlock(PREVIOUS_BLOCK);
	}

}
