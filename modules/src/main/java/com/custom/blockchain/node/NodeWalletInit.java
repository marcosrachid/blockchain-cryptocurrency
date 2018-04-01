package com.custom.blockchain.node;

import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_BLOCKS_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_MEMPOOL_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_PEERS_DIRECTORY;

import java.io.File;
import java.security.Security;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.component.WalletNetworkManager;
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

	private WalletNetworkManager networkManagement;

	public NodeWalletInit(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final CurrentBlockDB currentBlockDB, final UTXOChainstateDB utxoChainstateDB, final PeersDB peersDB,
			final MempoolDB mempoolDB, final BlockStateManagement blockStateManagement,
			final WalletNetworkManager networkManagement) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.currentBlockDB = currentBlockDB;
		this.utxoChainstateDB = utxoChainstateDB;
		this.peersDB = peersDB;
		this.mempoolDB = mempoolDB;
		this.blockStateManagement = blockStateManagement;
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
		File blocks = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_BLOCKS_DIRECTORY,
				blockchainProperties.getCoinName()));
		blocks.mkdirs();
		File chainstate = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_CHAINSTATE_DIRECTORY,
				blockchainProperties.getCoinName()));
		chainstate.mkdirs();
		File peers = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_PEERS_DIRECTORY,
				blockchainProperties.getCoinName()));
		peers.mkdirs();
		File mempool = new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_MEMPOOL_DIRECTORY,
				blockchainProperties.getCoinName()));
		mempool.mkdirs();

		// load node services for a simple wallet
		loadServices();

		// start thread for searching blocks and transactions
		LOG.info("[Crypto] Starting peer and actions searching thread...");
		this.networkManagement.searchPeers();
		this.networkManagement.startServer();
		this.networkManagement.checkPeersConnection();

		Block currentBlock = currentBlockDB.get();
		LOG.info("[Crypto] Current block state: " + currentBlock);
		if (currentBlock == null) {
			LOG.info("[Crypto] Starting first block on Blockchain");
			Wallet owner = new Wallet();
			Block genesis = BlockFactory.getGenesisBlock();
			genesis.setMiner(owner.getPublicKey());

			logKeys(owner);
			premined(genesis, owner);
			setGenesis(genesis);
		} else {
			LOG.info("[Crypto] Blockchain already");
			blockStateManagement.getNextBlock();
		}
	}

	/**
	 * 
	 */
	@Override
	@PreDestroy
	public void closeConnections() {
		LOG.info("[Crypto] closing connections...");
		currentBlockDB.close();
		utxoChainstateDB.close();
		peersDB.close();
		mempoolDB.close();
	}

	@Override
	protected void loadServices() {
		NodeStateManagement.SERVICES.add(Service.PING);
		NodeStateManagement.SERVICES.add(Service.PONG);
		NodeStateManagement.SERVICES.add(Service.GET_STATE);
		NodeStateManagement.SERVICES.add(Service.GET_STATE_RESPONSE);
		NodeStateManagement.SERVICES.add(Service.GET_BLOCK);
		NodeStateManagement.SERVICES.add(Service.GET_BLOCK_RESPONSE);
		NodeStateManagement.SERVICES.add(Service.GET_PEERS);
		NodeStateManagement.SERVICES.add(Service.GET_PEERS_RESPONSE);
		NodeStateManagement.SERVICES.add(Service.GET_TRANSACTIONS);
	}

}
