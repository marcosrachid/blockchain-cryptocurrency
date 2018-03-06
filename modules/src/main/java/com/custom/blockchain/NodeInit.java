package com.custom.blockchain;

import static com.custom.blockchain.properties.BlockchainImutableProperties.UTXOs;
import static com.custom.blockchain.properties.GenesisProperties.GENESIS_TX_ID;

import java.math.BigDecimal;
import java.security.Security;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.block.BlockType;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.FileUtil;
import com.custom.blockchain.util.components.BlockManagement;
import com.custom.blockchain.wallet.Wallet;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NodeInit {

	private static final Logger LOG = LoggerFactory.getLogger(NodeInit.class);

	@Value("${application.name:'Rachid Coin'}")
	private String coinName;

	@Value("${application.blockchain.coinbase:'default'}")
	private String coinbase;

	@Value("${application.blockchain.premined:100}")
	private BigDecimal premined;

	private BlockManagement blockManagement;

	public NodeInit(final BlockManagement blockManagement) {
		super();
		this.blockManagement = blockManagement;
	}

	/**
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void startBlocks() throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");

		// TODO: syncing

		if (!FileUtil.isBlockchainStarted(coinName)) {
			LOG.info("Starting first block on Blockchain");
			Block genesis = BlockFactory.getBlock(BlockType.GENESIS, null);
			Wallet owner = new Wallet();

			LOG.info("Ower wallet: " + owner);

			RewardTransaction genesisTransaction = new RewardTransaction(coinbase, owner.getPublicKey(), premined);

			genesisTransaction.setTransactionId(GENESIS_TX_ID);
			genesisTransaction.setOutput(new TransactionOutput(genesisTransaction.getReciepient(),
					genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
			UTXOs.put(genesisTransaction.getOutput().getId(), genesisTransaction.getOutput());

			blockManagement.setGenesisBlock(genesis);
			blockManagement.setPreviousBlock(genesis);
			blockManagement.setCurrentBlock(BlockFactory.getBlock(genesis));
		} else {
			LOG.info("Blockchain already started or premined is 0");
		}
	}

}
