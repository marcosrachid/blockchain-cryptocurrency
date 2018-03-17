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
import com.custom.blockchain.data.chainstate.ChainstateDB;
import com.custom.blockchain.node.network.client.component.ClientManager;
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
@Profile("miner")
@Component
public class NodeMinerInit extends AbstractNode {

	private static final Logger LOG = LoggerFactory.getLogger(NodeMinerInit.class);

	public NodeMinerInit(final ChainstateDB chainstateDb, final ObjectMapper objectMapper,
			final ClientManager clientManagement) {
		this.chainstateDb = chainstateDb;
		this.objectMapper = objectMapper;
		this.clientManagement = clientManagement;
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

		// load node services for a miner
		NodeStateManagement.services.add(Service.RECEIVE_BLOCKS);
		NodeStateManagement.services.add(Service.SEND_BLOCKS);
		NodeStateManagement.services.add(Service.RECEIVE_TRANSACTIONS);

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

}
