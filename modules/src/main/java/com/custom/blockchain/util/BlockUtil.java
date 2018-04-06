package com.custom.blockchain.util;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.data.block.BlockDB;

public final class BlockUtil {

	/**
	 * 
	 * @param blockDB
	 * @param block
	 * @return
	 */
	public static TransactionsBlock getLastTransactionBlock(BlockDB blockDB, AbstractBlock block) {
		if (block instanceof TransactionsBlock) {
			return (TransactionsBlock) block;
		}
		return getLastTransactionBlock(blockDB, blockDB.get(block.getHeight() - 1));
	}

}
