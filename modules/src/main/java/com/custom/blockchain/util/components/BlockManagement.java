package com.custom.blockchain.util.components;

import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockManagement {

	private Block genesisBlock;
	private Block previousBlock;
	private Block currentBlock;

	public Block getGenesisBlock() {
		return genesisBlock;
	}

	public void setGenesisBlock(Block genesisBlock) {
		this.genesisBlock = genesisBlock;
	}

	public Block getPreviousBlock() {
		return previousBlock;
	}

	public void setPreviousBlock(Block previousBlock) {
		this.previousBlock = previousBlock;
	}

	public Block getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentBlock(Block currentBlock) {
		this.currentBlock = currentBlock;
	}

}
