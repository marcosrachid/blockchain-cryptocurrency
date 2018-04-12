package com.custom.blockchain.node;

import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_BLOCKS_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_MEMPOOL_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_PEERS_DIRECTORY;

import java.io.File;
import java.security.Security;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.node.network.server.Server;
import com.custom.blockchain.node.network.server.dispatcher.Service;
import com.custom.blockchain.service.BlockService;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.util.OsUtil;
import com.custom.blockchain.wallet.Wallet;
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

	public NodeWalletInit(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final CurrentBlockDB currentBlockDB, final CurrentPropertiesBlockDB currentPropertiesBlockDB,
			final UTXOChainstateDB utxoChainstateDB, final MempoolDB mempoolDB, final BlockService blockService,
			final TransactionService transactionService, final BlockStateManagement blockStateManagement,
			final Server server, final @Qualifier("StartingProperties") PropertiesBlock propertiesBlock) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.utxoChainstateDB = utxoChainstateDB;
		this.mempoolDB = mempoolDB;
		this.blockService = blockService;
		this.transactionService = transactionService;
		this.blockStateManagement = blockStateManagement;
		this.server = server;
		this.propertiesBlock = currentPropertiesBlockDB.get();
		if (this.propertiesBlock == null) {
			this.propertiesBlock = propertiesBlock;
		}
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
		File blocks = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_BLOCKS_DIRECTORY, coinName));
		blocks.mkdirs();
		File chainstate = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_CHAINSTATE_DIRECTORY, coinName));
		chainstate.mkdirs();
		File peers = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_PEERS_DIRECTORY, coinName));
		peers.mkdirs();
		File mempool = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_MEMPOOL_DIRECTORY, coinName));
		mempool.mkdirs();

		// load node services for a simple wallet
		loadServices();

		AbstractBlock currentBlock = currentBlockDB.get();
		LOG.info("[Crypto] Current block state: " + currentBlock);
		if (currentBlock == null) {
			LOG.info("[Crypto] Starting first block on Blockchain");
			Wallet owner = new Wallet();

			logKeys(owner);
			setGenesis(propertiesBlock);
			TransactionsBlock premined = blockStateManagement.getNextBlock();
			premined.setMiner(owner.getPublicKey());
			premined(premined, owner);
		} else {
			LOG.info("[Crypto] Blockchain already");
			blockStateManagement.getNextBlock();
		}
	}

	@Override
	protected void loadServices() {
		NodeStateManagement.SERVICES.add(Service.GET_STATE);
		NodeStateManagement.SERVICES.add(Service.GET_STATE_RESPONSE);
		NodeStateManagement.SERVICES.add(Service.GET_BLOCK);
		NodeStateManagement.SERVICES.add(Service.GET_BLOCK_RESPONSE);
		NodeStateManagement.SERVICES.add(Service.GET_PEERS);
		NodeStateManagement.SERVICES.add(Service.GET_PEERS_RESPONSE);
		NodeStateManagement.SERVICES.add(Service.GET_TRANSACTIONS);
	}

}
