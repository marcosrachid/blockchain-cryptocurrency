package com.custom.blockchain.node;

import static com.custom.blockchain.costants.SystemConstants.BLOCKS_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static com.custom.blockchain.transaction.component.TransactionMempool.TRANSACTION_MEMPOOL;

import java.io.File;
import java.security.Security;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.ChainstateDB;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.component.NetworkManager;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.util.FileUtil;
import com.custom.blockchain.util.OsUtil;
import com.custom.blockchain.wallet.Wallet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Profile("!miner")
@Component
public class NodeWalletInit extends AbstractNode {

	private static final Logger LOG = LoggerFactory.getLogger(NodeWalletInit.class);

	public NodeWalletInit(final BlockchainProperties blockchainProperties, final ChainstateDB chainstateDb,
			final ObjectMapper objectMapper, final NetworkManager networkManagement) {
		this.blockchainProperties = blockchainProperties;
		this.chainstateDb = chainstateDb;
		this.objectMapper = objectMapper;
		this.networkManagement = networkManagement;
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Override
	@PostConstruct
	public void startBlocks() throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");

		// creating data storage path if not exists
		File blocks = new File(
				String.format(OsUtil.getRootDirectory() + BLOCKS_DIRECTORY, blockchainProperties.getCoinName()));
		blocks.mkdirs();
		File chainstate = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_CHAINSTATE_DIRECTORY,
				blockchainProperties.getCoinName()));
		chainstate.mkdirs();

		// start thread for searching blocks and transactions
		LOG.info("[Crypto] Starting peer and actions searching thread...");
		this.networkManagement.searchPeers();
		this.networkManagement.startServer();
		this.networkManagement.pingPeers();

		// read current Transaction mempool
		TRANSACTION_MEMPOOL = objectMapper.readValue(
				FileUtil.readUnminedTransaction(blockchainProperties.getCoinName()),
				new TypeReference<Set<Transaction>>() {
				});

		// load node services for a simple wallet
		loadServices();

		if (!FileUtil.isBlockchainStarted(blockchainProperties.getCoinName())) {
			LOG.info("[Crypto] Starting first block on Blockchain");
			Block genesis = BlockFactory.getGenesisBlock(blockchainProperties.getCoinName());
			Wallet owner = new Wallet();

			logKeys(owner);
			premined(owner);
			setBlockState(genesis);
		} else {
			LOG.info("[Crypto] Blockchain already");
		}
	}

	@Override
	protected void loadServices() {
		NodeStateManagement.SERVICES.add(Service.GET_STATE);
		NodeStateManagement.SERVICES.add(Service.GET_BLOCK);
		NodeStateManagement.SERVICES.add(Service.GET_PEERS);
	}

}
