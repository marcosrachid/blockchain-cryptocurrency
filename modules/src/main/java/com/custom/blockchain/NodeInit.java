package com.custom.blockchain;

import static com.custom.blockchain.properties.BlockchainImutableProperties.PREMINED;
import static com.custom.blockchain.properties.BlockchainImutableProperties.UTXOs;
import static com.custom.blockchain.properties.GenesisProperties.GENESIS_TX_ID;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.block.BlockType;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.components.BlockManagement;
import com.custom.blockchain.util.components.WalletManagement;
import com.custom.blockchain.wallet.Wallet;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NodeInit {

	private static final Logger LOG = LoggerFactory.getLogger(NodeInit.class);

	private WalletManagement walletManagement;
	private BlockManagement blockManagement;
	private TransactionService transactionService;

	public NodeInit(final WalletManagement walletManagement, final BlockManagement blockManagement,
			final TransactionService transactionService) {
		super();
		this.walletManagement = walletManagement;
		this.blockManagement = blockManagement;
		this.transactionService = transactionService;
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
	 * @throws Exception
	 */
	@PostConstruct
	public void startBlocks() throws Exception {
		Block genesis = BlockFactory.getBlock(BlockType.GENESIS, null);
		Wallet coinbase = new Wallet();
		Wallet owner = new Wallet();

		LOG.info("Coinbase wallet: " + coinbase);
		LOG.info("Ower wallet: " + owner);

		Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), owner.getPublicKey(), PREMINED, null);
		transactionService.generateSignature(genesisTransaction, coinbase);
		genesisTransaction.setTransactionId(GENESIS_TX_ID);
		genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getReciepient(),
				genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
		UTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

		walletManagement.setCoinbase(coinbase);
		blockManagement.setGenesisBlock(genesis);
		blockManagement.setPreviousBlock(genesis);
		blockManagement.setCurrentBlock(BlockFactory.getBlock(genesis));
	}

}
