package com.custom.blockchain.block;

import static com.custom.blockchain.properties.BlockchainImutableProperties.BLOCKCHAIN;
import static com.custom.blockchain.properties.GenesisProperties.GENESIS_PREVIOUS_HASH;

import com.custom.blockchain.block.exception.BlockException;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockFactory {

	/**
	 * 
	 * @param blockType
	 * @param previousBlock
	 * @return
	 * @throws BlockException
	 */
	public static Block getBlock(BlockType blockType, Block previousBlock) throws BlockException {
		if (blockType == BlockType.GENESIS) {
			if (!BLOCKCHAIN.isEmpty()) {
				throw new BlockException("Blockchain already started to create genesis block");
			}
			return new Block(GENESIS_PREVIOUS_HASH);
		} else {
			return new Block(previousBlock.getHash());
		}
	}

	/**
	 * 
	 * @param previousBlock
	 * @return
	 */
	public static Block getBlock(Block previousBlock) {
		return new Block(previousBlock.getHash());
	}

}
