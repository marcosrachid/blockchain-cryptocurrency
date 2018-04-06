package com.custom.blockchain.node;

import java.math.BigDecimal;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.node.network.server.Server;
import com.custom.blockchain.node.network.server.SocketThread;
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

	@Value("${application.blockchain.coinName}")
	protected String coinName;

	protected ObjectMapper objectMapper;

	protected BlockchainProperties blockchainProperties;

	protected CurrentBlockDB currentBlockDB;

	protected CurrentPropertiesBlockDB currentPropertiesBlockDB;

	protected UTXOChainstateDB utxoChainstateDB;

	protected PeersDB peersDB;

	protected MempoolDB mempoolDB;

	protected BlockStateManagement blockStateManagement;

	protected Server server;

	protected PropertiesBlock propertiesBlock;

	public abstract void startBlocks() throws Exception;

	@PreDestroy
	public void closeConnections() {
		LOG.info("[Crypto] closing connections...");
		SocketThread.inactivate();
		server.stop();
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
	protected void premined(final TransactionsBlock premined, Wallet owner) {
		if (propertiesBlock.getPremined() == null || propertiesBlock.getPremined().compareTo(BigDecimal.ZERO) == 0)
			return;
		RewardTransaction preminedTransaction = new RewardTransaction(propertiesBlock.getCoinbase(),
				propertiesBlock.getPremined(), propertiesBlock.getStartingDifficulty());
		preminedTransaction.setTransactionId(GENESIS_TX_ID);
		preminedTransaction.setOutput(new TransactionOutput(owner.getPublicKey(), preminedTransaction.getValue(),
				preminedTransaction.getTransactionId()));
		premined.getTransactions().add(preminedTransaction);
		premined.setMerkleRoot(TransactionUtil.getMerkleRoot(premined.getTransactions()));
		blockStateManagement.foundBlock(premined);
		LOG.info("Premined transaction: {}", premined.getTransactions());
	}

	/**
	 * 
	 * @param genesis
	 */
	protected void setGenesis(PropertiesBlock genesis) {
		blockStateManagement.foundBlock(genesis);
	}

}
