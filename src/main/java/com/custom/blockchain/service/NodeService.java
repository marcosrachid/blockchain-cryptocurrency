package com.custom.blockchain.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.chainstate.CurrentBlockChainstateDB;
import com.custom.blockchain.data.chainstate.CurrentCirculatingSupplyChainstateDB;
import com.custom.blockchain.util.BlockUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class NodeService {

	private BlockDB blockDB;

	private CurrentBlockChainstateDB currentBlockDB;

	private CurrentCirculatingSupplyChainstateDB currentCirculatingSupplyChainstateDB;

	public NodeService(final BlockDB blockDB, final CurrentBlockChainstateDB currentBlockDB,
			final CurrentCirculatingSupplyChainstateDB currentCirculatingSupplyChainstateDB) {
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.currentCirculatingSupplyChainstateDB = currentCirculatingSupplyChainstateDB;
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

	/**
	 * 
	 * @return
	 */
	public BigDecimal getCurrentCirculatingSupply() {
		return currentCirculatingSupplyChainstateDB.get();
	}

}
