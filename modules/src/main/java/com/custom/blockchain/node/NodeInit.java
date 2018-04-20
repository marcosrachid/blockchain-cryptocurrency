package com.custom.blockchain.node;

import static com.custom.blockchain.constants.SystemConstants.LEVEL_DB_BLOCKS_DIRECTORY;
import static com.custom.blockchain.constants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static com.custom.blockchain.constants.SystemConstants.LEVEL_DB_MEMPOOL_DIRECTORY;
import static com.custom.blockchain.constants.SystemConstants.LEVEL_DB_PEERS_DIRECTORY;

import java.io.File;
import java.math.BigDecimal;
import java.security.Security;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.data.chainstate.CurrentBlockChainstateDB;
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.node.network.server.Server;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.service.BlockService;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.service.WalletService;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.OsUtil;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NodeInit {

	private static final Logger LOG = LoggerFactory.getLogger(NodeInit.class);

	@Value("${application.blockchain.coinName}")
	private String coinName;

	private CurrentBlockChainstateDB currentBlockDB;

	private UTXOChainstateDB utxoChainstateDB;

	private PeersDB peersDB;

	private MempoolDB mempoolDB;

	private BlockService blockService;

	private WalletService walletService;

	private TransactionService transactionService;

	private BlockStateManagement blockStateManagement;

	private Server server;

	private PropertiesBlock propertiesBlock;

	public NodeInit(final CurrentBlockChainstateDB currentBlockDB,
			final CurrentPropertiesChainstateDB currentPropertiesBlockDB, final UTXOChainstateDB utxoChainstateDB,
			final MempoolDB mempoolDB, final BlockService blockService, final WalletService walletService,
			final TransactionService transactionService, final BlockStateManagement blockStateManagement,
			final Server server, final @Qualifier("StartingProperties") PropertiesBlock propertiesBlock) {
		this.currentBlockDB = currentBlockDB;
		this.utxoChainstateDB = utxoChainstateDB;
		this.mempoolDB = mempoolDB;
		this.blockService = blockService;
		this.walletService = walletService;
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

	/**
	 * 
	 */
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
	 * 
	 * @param owner
	 */
	private void logKeys(Wallet owner) {
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
	private void premined(final TransactionsBlock premined, Wallet owner) {
		if (propertiesBlock.getPremined() == null || propertiesBlock.getPremined().compareTo(BigDecimal.ZERO) == 0)
			return;
		LOG.info("[Crypto] Mining the premined block...");
		String target = StringUtil.getDificultyString(propertiesBlock.getStartingDifficulty());
		RewardTransaction preminedTransaction = new RewardTransaction(propertiesBlock.getCoinbase(),
				propertiesBlock.getPremined(), propertiesBlock.getStartingDifficulty());
		preminedTransaction.setTransactionId(transactionService.calulateHash(preminedTransaction));
		preminedTransaction.setOutput(new TransactionOutput(owner.getPublicKey(), preminedTransaction.getValue(),
				preminedTransaction.getTransactionId()));
		premined.getTransactions().add(preminedTransaction);
		premined.setMerkleRoot(TransactionUtil.getMerkleRoot(premined.getTransactions()));
		premined.setHash(blockService.calculateHash(premined));
		while (!premined.getHash().substring(0, propertiesBlock.getStartingDifficulty()).equals(target)) {
			premined.setNonce(premined.getNonce() + 1);
			premined.setHash(blockService.calculateHash(premined));
		}
		blockStateManagement.foundBlock(premined);
		walletService.useNewWallet(owner);
		LOG.info("Premined transaction: {}", premined.getTransactions());
	}

	/**
	 * 
	 * @param genesis
	 */
	private void setGenesis(PropertiesBlock genesis) {
		blockStateManagement.foundBlock(genesis);
	}

}
