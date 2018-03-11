package com.custom.blockchain.block;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockFactory {

	/**
	 * 
	 * @param coinName
	 * @return
	 */
	public static Block getGenesisBlock(String coinName) {
		return new Block();
	}

	/**
	 * 
	 * @param previousBlock
	 * @return
	 */
	public static Block getBlock(Block previousBlock) {
		return new Block(previousBlock);
	}

}
