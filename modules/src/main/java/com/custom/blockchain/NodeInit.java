package com.custom.blockchain;

import static com.custom.blockchain.costants.ChainConstants.TRANSACTION_MEMPOOL;
import static com.custom.blockchain.costants.SystemConstants.BLOCKS_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static com.custom.blockchain.properties.BlockchainImutableProperties.GENESIS_TX_ID;
import static com.custom.blockchain.properties.BlockchainMutableProperties.CURRENT_BLOCK;
import static com.custom.blockchain.properties.BlockchainMutableProperties.GENESIS_BLOCK;
import static com.custom.blockchain.properties.BlockchainMutableProperties.PREVIOUS_BLOCK;

import java.io.File;
import java.math.BigDecimal;
import java.security.Security;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.data.chainstate.ChainstateDB;
import com.custom.blockchain.network.client.component.ClientManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.FileUtil;
import com.custom.blockchain.util.OsUtil;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	private ChainstateDB chainstateDb;

	private ObjectMapper objectMapper;

	private ClientManager clientManagement;

	public NodeInit(final ChainstateDB chainstateDb, final ObjectMapper objectMapper,
			final ClientManager clientManagement) {
		this.chainstateDb = chainstateDb;
		this.objectMapper = objectMapper;
		this.clientManagement = clientManagement;
	}

	/**
	 * 
	 * @throws Exception
	 */
	@PostConstruct
	public void startBlocks() throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");

		// creating data storage path if not exists
		File blocks = new File(String.format(OsUtil.getRootDirectory() + BLOCKS_DIRECTORY, coinName));
		blocks.mkdirs();
		File chainstate = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_CHAINSTATE_DIRECTORY, coinName));
		chainstate.mkdirs();

		// start thread for searching blocks and transactions
		LOG.info("[Crypto] Starting peer and actions searching thread...");
		this.clientManagement.searchActions();

		// read current Transaction mempool
		TRANSACTION_MEMPOOL = objectMapper.readValue(FileUtil.readUnminedTransaction(coinName),
				new TypeReference<Set<Transaction>>() {
				});

		if (!FileUtil.isBlockchainStarted(coinName)) {
			LOG.info("[Crypto] Starting first block on Blockchain");
			Block genesis = BlockFactory.getGenesisBlock(coinName);
			Wallet owner = new Wallet();

			logKeys(owner);
			premined(owner);
			setBlockState(genesis);
		} else {
			LOG.info("[Crypto] Blockchain already");
		}
	}

	/**
	 * 
	 * @param owner
	 */
	private void logKeys(Wallet owner) {
		int ownerLength = StringUtil.getBiggestLength(WalletUtil.getStringFromKey(owner.getPublicKey()),
				WalletUtil.getStringFromKey(owner.getPrivateKey())) - 1;

		LOG.info("[Crypto] ##################" + StringUtil.repeat('#', ownerLength) + "####");
		LOG.info("[Crypto] ##################" + StringUtil.repeat('#', ownerLength) + "####");
		LOG.info("[Crypto] ##################" + StringUtil.repeat('#', ownerLength) + "####");
		LOG.info("[Crypto] ### Owner public key:  " + WalletUtil.getStringFromKey(owner.getPublicKey()) + " ###");
		LOG.info("[Crypto] ### Owner private key: " + WalletUtil.getStringFromKey(owner.getPrivateKey()) + " ###");
		LOG.info("[Crypto] ##################" + StringUtil.repeat('#', ownerLength) + "####");
		LOG.info("[Crypto] ##################" + StringUtil.repeat('#', ownerLength) + "####");
		LOG.info("[Crypto] ##################" + StringUtil.repeat('#', ownerLength) + "####");
	}

	/**
	 * 
	 * @param owner
	 */
	private void premined(Wallet owner) {
		RewardTransaction genesisTransaction = new RewardTransaction(coinbase, premined);

		genesisTransaction.setOutput(new TransactionOutput(owner.getPublicKey(), genesisTransaction.getValue()));
		genesisTransaction.setTransactionId(GENESIS_TX_ID);
		chainstateDb.put("c" + genesisTransaction.getOutput().getId(), genesisTransaction.getOutput());
		LOG.info("Premined transaction: {}", chainstateDb.get("c" + genesisTransaction.getOutput().getId()));
	}

	/**
	 * 
	 * @param genesis
	 */
	private void setBlockState(Block genesis) {
		GENESIS_BLOCK = genesis;
		PREVIOUS_BLOCK = genesis;
		CURRENT_BLOCK = BlockFactory.getBlock(genesis);
	}

}
