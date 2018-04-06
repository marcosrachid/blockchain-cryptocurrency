package com.custom.blockchain.service;

import org.springframework.stereotype.Service;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.util.BlockUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class NodeService {

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	public NodeService(final BlockDB blockDB, final CurrentBlockDB currentBlockDB) {
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
	}

	public AbstractBlock getCurrentBlock() {
		return this.currentBlockDB.get();
	}

	/**
	 * 
	 * @return
	 */
	public Integer getCurrentDifficulty() {
		return BlockUtil.getLastTransactionBlock(blockDB, getCurrentBlock()).getRewardTransaction().getDifficulty();
	}

	/**
	 * 
	 * @return
	 */
	public Long getCurrentHeight() {
		return getCurrentBlock().getHeight();
	}

}
