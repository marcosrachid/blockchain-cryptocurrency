package com.custom.blockchain.service;

import org.springframework.stereotype.Service;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.data.block.CurrentBlockDB;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class NodeService {

	private CurrentBlockDB currentBlockDB;

	public NodeService(final CurrentBlockDB currentBlockDB) {
		this.currentBlockDB = currentBlockDB;
	}
	
	public Block getCurrentBlock() {
		return this.currentBlockDB.get();
	}

	/**
	 * 
	 * @return
	 */
	public Integer getCurrentDifficulty() {
		return getCurrentBlock().getRewardTransaction().getDifficulty();
	}

	/**
	 * 
	 * @return
	 */
	public Long getCurrentHeight() {
		return getCurrentBlock().getHeight();
	}

}
