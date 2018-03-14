package com.custom.blockchain;

import static com.custom.blockchain.costants.ChainConstants.TRANSACTION_MEMPOOL;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_BLOCKS_INDEX_DIRECTORY;
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
import com.custom.blockchain.data.blockindex.BlockIndexDB;
import com.custom.blockchain.data.chainstate.ChainstateDB;
import com.custom.blockchain.network.client.component.ClientManagement;
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

	private BlockIndexDB blockIndexDb;

	private ChainstateDB chainstateDb;

	private ObjectMapper objectMapper;

	private ClientManagement clientManagement;

	public NodeInit(final BlockIndexDB blockIndexDb, final ChainstateDB chainstateDb, final ObjectMapper objectMapper,
			final ClientManagement clientManagement) {
		this.blockIndexDb = blockIndexDb;
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
		File blockIndexes = new File(
				String.format(OsUtil.getRootDirectory() + LEVEL_DB_BLOCKS_INDEX_DIRECTORY, coinName));
		blockIndexes.mkdirs();
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

			RewardTransaction genesisTransaction = new RewardTransaction(coinbase, owner.getPublicKey(), premined);

			genesisTransaction.setTransactionId(GENESIS_TX_ID);
			genesisTransaction.setOutput(new TransactionOutput(genesisTransaction.getReciepient(),
					genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
			chainstateDb.put("c" + genesisTransaction.getOutput().getId(), genesisTransaction.getOutput());
			LOG.info("Premined transaction: {}", chainstateDb.get("c" + genesisTransaction.getOutput().getId()));

			GENESIS_BLOCK = genesis;
			PREVIOUS_BLOCK = genesis;
			CURRENT_BLOCK = BlockFactory.getBlock(genesis);
		} else {
			LOG.info("[Crypto] Blockchain already");
		}
	}

}
