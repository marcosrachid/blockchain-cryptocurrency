package com.custom.blockchain;

import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_BLOCKS_INDEX_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static com.custom.blockchain.properties.BlockchainImutableProperties.GENESIS_TX_ID;
import static com.custom.blockchain.properties.BlockchainMutableProperties.CURRENT_BLOCK;
import static com.custom.blockchain.properties.BlockchainMutableProperties.GENESIS_BLOCK;
import static com.custom.blockchain.properties.BlockchainMutableProperties.PREVIOUS_BLOCK;

import java.io.File;
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
import com.custom.blockchain.network.client.Client;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.FileUtil;
import com.custom.blockchain.util.OsUtil;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;
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

		Client.start();

		// TODO: syncing

		if (!FileUtil.isBlockchainStarted(coinName)) {
			LOG.info("Starting first block on Blockchain");
			Block genesis = BlockFactory.getBlock(BlockType.GENESIS, null);
			Wallet owner = new Wallet();
			int ownerLength = StringUtil.getBiggestLength(TransactionUtil.getStringFromKey(owner.getPublicKey()),
					TransactionUtil.getStringFromKey(owner.getPrivateKey())) - 1;

			LOG.info("##################" + StringUtil.repeat('#', ownerLength) + "####");
			LOG.info("##################" + StringUtil.repeat('#', ownerLength) + "####");
			LOG.info("##################" + StringUtil.repeat('#', ownerLength) + "####");
			LOG.info("### Owner public key:  " + TransactionUtil.getStringFromKey(owner.getPublicKey()) + " ###");
			LOG.info("### Owner private key: " + TransactionUtil.getStringFromKey(owner.getPrivateKey()) + " ###");
			LOG.info("##################" + StringUtil.repeat('#', ownerLength) + "####");
			LOG.info("##################" + StringUtil.repeat('#', ownerLength) + "####");
			LOG.info("##################" + StringUtil.repeat('#', ownerLength) + "####");

			RewardTransaction genesisTransaction = new RewardTransaction(coinbase, owner.getPublicKey(), premined);

			genesisTransaction.setTransactionId(GENESIS_TX_ID);
			genesisTransaction.setOutput(new TransactionOutput(genesisTransaction.getReciepient(),
					genesisTransaction.getValue(), genesisTransaction.getTransactionId()));
			// TODO: add first transaction to blk0.dat

			GENESIS_BLOCK = genesis;
			PREVIOUS_BLOCK = genesis;
			CURRENT_BLOCK = BlockFactory.getBlock(genesis);
		} else {
			LOG.info("Blockchain already started or premined is 0");
		}
	}

}
