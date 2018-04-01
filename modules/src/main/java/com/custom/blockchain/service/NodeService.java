package com.custom.blockchain.service;

import org.springframework.stereotype.Service;

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

	public Integer getCurrentDifficulty() {
		return this.currentBlockDB.get().getRewardTransaction().getDifficulty();
	}

}
