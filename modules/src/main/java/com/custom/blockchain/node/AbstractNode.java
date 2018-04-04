package com.custom.blockchain.node;

import java.math.BigDecimal;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.node.network.server.Server;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractNode {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractNode.class);

	protected static final String GENESIS_TX_ID = "0";

	protected ObjectMapper objectMapper;

	protected BlockchainProperties blockchainProperties;

	protected CurrentBlockDB currentBlockDB;

	protected UTXOChainstateDB utxoChainstateDB;

	protected PeersDB peersDB;

	protected MempoolDB mempoolDB;

	protected BlockStateManagement blockStateManagement;

	protected Server peerListener;

	public abstract void startBlocks() throws Exception;

	@PreDestroy
	public void closeConnections() {
		LOG.info("[Crypto] closing connections...");
		peerListener.stop();
		currentBlockDB.close();
		utxoChainstateDB.close();
		peersDB.close();
		mempoolDB.close();
	}

	/**
	 * Services able to receive
	 */
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
	protected void premined(final Block genesis, Wallet owner) {
		if (blockchainProperties.getPremined() == null
				|| blockchainProperties.getPremined().compareTo(BigDecimal.ZERO) == 0)
			return;
		RewardTransaction genesisTransaction = new RewardTransaction(blockchainProperties.getCoinbase(),
				blockchainProperties.getPremined(), blockchainProperties.getStartingDifficulty());
		genesisTransaction.setTransactionId(GENESIS_TX_ID);
		genesisTransaction.setOutput(new TransactionOutput(owner.getPublicKey(), genesisTransaction.getValue(),
				genesisTransaction.getTransactionId()));
		genesis.getTransactions().add(genesisTransaction);
		genesis.setMerkleRoot(TransactionUtil.getMerkleRoot(genesis.getTransactions()));
		LOG.info("Premined transaction: {}", genesis.getTransactions());
	}

	/**
	 * 
	 * @param genesis
	 */
	protected void setGenesis(Block genesis) {
		blockStateManagement.foundBlock(genesis);
	}

}
