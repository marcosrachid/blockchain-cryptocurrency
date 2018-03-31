package com.custom.blockchain.block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOG = LoggerFactory.getLogger(BlockStateManagement.class);

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
		LOG.info("[Crypto] Found new block: " + block);
		blockDB.put(block.getHeight(), block);
		currentBlockDB.put(block);
		nextBlock = BlockFactory.getBlock(block);

	}

	/**
	 * 
	 * @return
	 */
	public Block getNextBlock() {
		Block currentBlock = currentBlockDB.get();
		LOG.trace("[Crypto] Retrieving current Block: " + currentBlock);
		if (nextBlock == null) {
			nextBlock = BlockFactory.getBlock(currentBlock);
		}
		return nextBlock;
	}

}
