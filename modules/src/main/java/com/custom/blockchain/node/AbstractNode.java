package com.custom.blockchain.node;

import static com.custom.blockchain.block.BlockStateManagement.CURRENT_BLOCK;
import static com.custom.blockchain.block.BlockStateManagement.GENESIS_BLOCK;
import static com.custom.blockchain.block.BlockStateManagement.PREVIOUS_BLOCK;
import static com.custom.blockchain.costants.ChainConstants.GENESIS_TX_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockFactory;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.ChainstateDB;
import com.custom.blockchain.node.network.client.component.ClientManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractNode {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractNode.class);

	protected BlockchainProperties blockchainProperties;

	protected ChainstateDB chainstateDb;

	protected ObjectMapper objectMapper;

	protected ClientManager clientManagement;

	public abstract void startBlocks() throws Exception;

	protected abstract void loadServices();

	/**
	 * 
	 * @param owner
	 */
	protected void logKeys(Wallet owner) {
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
	protected void premined(Wallet owner) {
		RewardTransaction genesisTransaction = new RewardTransaction(blockchainProperties.getCoinbase(),
				blockchainProperties.getPremined());
		genesisTransaction.setTransactionId(GENESIS_TX_ID);
		genesisTransaction.setOutput(new TransactionOutput(owner.getPublicKey(), genesisTransaction.getValue(),
				genesisTransaction.getTransactionId()));
		chainstateDb.put("c" + genesisTransaction.getOutput().getId(), genesisTransaction.getOutput());
		LOG.info("Premined transaction: {}", chainstateDb.get("c" + genesisTransaction.getOutput().getId()));
	}

	/**
	 * 
	 * @param genesis
	 */
	protected void setBlockState(Block genesis) {
		GENESIS_BLOCK = genesis;
		PREVIOUS_BLOCK = genesis;
		CURRENT_BLOCK = BlockFactory.getBlock(genesis);
	}

}
