package com.custom.blockchain;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.block.BlockType;
import com.custom.blockchain.block.Genesis;
import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.block.management.BlockManagement;
import com.custom.blockchain.service.BlockService;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NodeInit {

	private BlockManagement blockManagement;
	private BlockService blockService;

	public NodeInit(final BlockManagement blockManagement, final BlockService blockService) {
		this.blockManagement = blockManagement;
		this.blockService = blockService;
	}

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@PostConstruct
	public void environment() throws NoSuchAlgorithmException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");
	}

	/**
	 * 
	 * @throws BlockException
	 */
	@PostConstruct
	public void genesis() throws BlockException {
		Genesis genesis = (Genesis) BlockFactory.getBlock(BlockType.GENESIS, null);
		blockService.mineBlock(genesis);
		blockManagement.setGenesisBlock(genesis);
		blockManagement.setPreviousBlock(genesis);
		blockManagement.setCurrentBlock(BlockFactory.getBlock(genesis));
	}

}
