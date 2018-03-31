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
	public static Block getGenesisBlock() {
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
