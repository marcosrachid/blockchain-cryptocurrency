package com.custom.blockchain.node;

import static com.custom.blockchain.costants.ChainConstants.BLK_DAT_INITIAL_VALUE;
import static com.custom.blockchain.costants.ChainConstants.GENESIS_TX_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.blockindex.CurrentFileBlockIndexDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.node.network.component.NetworkManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractNode {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractNode.class);

	protected BlockchainProperties blockchainProperties;

	protected UTXOChainstateDB utxoChainstateDb;

	protected CurrentFileBlockIndexDB currentFileBlockIndexDB;

	protected ObjectMapper objectMapper;

	protected NetworkManager networkManagement;

	protected BlockStateManagement blockStateManagement;

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
		utxoChainstateDb.put(genesisTransaction.getOutput().getId(), genesisTransaction.getOutput());
		LOG.info("Premined transaction: {}", utxoChainstateDb.get(genesisTransaction.getOutput().getId()));
	}

	/**
	 * 
	 * @param genesis
	 */
	protected void setGenesis(Block genesis) {
		try {
			currentFileBlockIndexDB.put(BLK_DAT_INITIAL_VALUE);
			blockStateManagement.foundBlock(genesis);
		} catch (BlockException e) {
			LOG.error("Premined block error: " + e.getMessage());
		}
	}

}
