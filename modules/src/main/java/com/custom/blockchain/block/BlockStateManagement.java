package com.custom.blockchain.block;

import org.springframework.stereotype.Component;

import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockStateManagement {

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	private Block nextBlock;

	public BlockStateManagement(final BlockDB blockDB, final CurrentBlockDB currentBlockDB) {
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
	}

	/**
	 * 
	 * @param block
	 * @throws BlockException
	 */
	public void foundBlock(Block block) throws BlockException {
		blockDB.put(block.getHeight(), block);
		currentBlockDB.put(block);
		nextBlock = BlockFactory.getBlock(block);
	}

	/**
	 * 
	 * @return
	 */
	public Block getNextBlock() {
		return nextBlock;
	}

}
