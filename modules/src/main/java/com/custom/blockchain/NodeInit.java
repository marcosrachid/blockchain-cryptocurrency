package com.custom.blockchain;

import static com.custom.blockchain.properties.BlockchainProperties.VERSION;

import java.security.Security;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.block.BlockType;
import com.custom.blockchain.block.Genesis;
import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.block.management.BlockManagement;
import com.custom.blockchain.service.BlockService;

@Component
public class NodeInit {

	@Value("${application.blockchain.version}")
	private String version;

	private BlockManagement blockManagement;
	private BlockService blockService;

	public NodeInit(final BlockManagement blockManagement, final BlockService blockService) {
		this.blockManagement = blockManagement;
		this.blockService = blockService;
	}

	@PostConstruct
	public void environment() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		VERSION = this.version;
	}

	@PostConstruct
	public void genesis() throws BlockException {
		Genesis genesis = (Genesis) BlockFactory.getBlock(BlockType.GENESIS, null);
		blockService.mineBlock(genesis);
		blockManagement.setGenesisBlock(genesis);
		blockManagement.setPreviousBlock(genesis);
		blockManagement.setCurrentBlock(BlockFactory.getBlock(genesis));
	}

}
