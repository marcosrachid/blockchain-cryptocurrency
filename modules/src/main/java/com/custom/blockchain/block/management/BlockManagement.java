package com.custom.blockchain.block.management;

import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.Genesis;

@Component
public class BlockManagement {

	private Genesis genesisBlock;
	private Block previousBlock;
	private Block currentBlock;

	public Genesis getGenesisBlock() {
		return genesisBlock;
	}

	public void setGenesisBlock(Genesis genesisBlock) {
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
