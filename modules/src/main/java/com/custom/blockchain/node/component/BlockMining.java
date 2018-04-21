package com.custom.blockchain.node.component;

import static com.custom.blockchain.constants.SystemConstants.DIFFICULTY_ADJUSTMENT_BLOCK;
import static com.custom.blockchain.node.NodeStateManagement.MINING_THREAD;
import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.chainstate.CurrentBlockChainstateDB;
import com.custom.blockchain.data.chainstate.CurrentCirculatingSupplyChainstateDB;
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.node.NodeStateManagement;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.node.network.server.dispatcher.Service;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
import com.custom.blockchain.node.network.server.request.arguments.BlockResponseArguments;
import com.custom.blockchain.service.BlockService;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.BlockUtil;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.util.WalletUtil;

@Profile("miner")
@Component
public class BlockMining {

	private static final Logger LOG = LoggerFactory.getLogger(BlockMining.class);

	private BlockchainProperties blockchainProperties;

	private BlockDB blockDB;

	private CurrentBlockChainstateDB currentBlockDB;

	private CurrentPropertiesChainstateDB currentPropertiesBlockDB;

	private CurrentCirculatingSupplyChainstateDB currentCirculatingSupplyChainstateDB;

	private MempoolDB mempoolDB;

	private BlockService blockService;

	private TransactionService transactionService;

	private BlockStateManagement blockStateManagement;

	private DifficultyAdjustment difficultyAdjustment;

	public BlockMining(final BlockchainProperties blockchainProperties, final BlockDB blockDB,
			final CurrentBlockChainstateDB currentBlockDB, final CurrentPropertiesChainstateDB currentPropertiesBlockDB,
			final CurrentCirculatingSupplyChainstateDB currentCirculatingSupplyChainstateDB, final MempoolDB mempoolDB,
			final BlockService blockService, final TransactionService transactionService,
			final BlockStateManagement blockStateManagement, final DifficultyAdjustment difficultyAdjustment) {
		this.blockchainProperties = blockchainProperties;
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.currentCirculatingSupplyChainstateDB = currentCirculatingSupplyChainstateDB;
		this.mempoolDB = mempoolDB;
		this.blockService = blockService;
		this.transactionService = transactionService;
		this.blockStateManagement = blockStateManagement;
		this.difficultyAdjustment = difficultyAdjustment;
	}

	@Scheduled(fixedRate = 5000)
	public void mine() {
		if (MINING_THREAD == null || !MINING_THREAD.isAlive())
			run();
	}

	/**
	 * 
	 */
	private void run() {
		MINING_THREAD = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					mineBlock();
				} catch (Exception e) {
					LOG.error("[Crypto] Could not mine: " + e.getMessage());
				}
			}

		});

		MINING_THREAD.start();
	}

	/**
	 * 
	 * @param block
	 * @throws BusinessException
	 */
	public void mineBlock() throws BusinessException {
		try {
			LOG.info("[Crypto] Preparing to mine new block...");
			Thread.sleep(5000l);
		} catch (InterruptedException e1) {
		}
		AbstractBlock currentBlock = currentBlockDB.get();
		if (!NodeStateManagement.isSynchronized(currentBlock.getHeight())) {
			LOG.info("[Crypto] Skip mining due to non synchronized node...");
			mineBlock();
		}
		DBIterator iterator = mempoolDB.iterator();
		if (!iterator.hasNext()) {
			LOG.info("[Crypto] Skip mining due to no transaction to on pool...");
			mineBlock();
		}
		List<SimpleTransaction> poolTransactions = new ArrayList<>();
		do {
			poolTransactions.add(mempoolDB.next(iterator));
		} while (iterator.hasNext());
		if (poolTransactions.isEmpty()) {
			LOG.info("[Crypto] Pool with empty transactions...");
			mineBlock();
		}
		long currentTimeInMillis = System.currentTimeMillis();
		TransactionsBlock block = blockStateManagement.getNextBlock();

		PropertiesBlock propertiesBlock = currentPropertiesBlockDB.get();

		Integer difficulty = null;
		if (block.getHeight() % DIFFICULTY_ADJUSTMENT_BLOCK == 0) {
			difficulty = difficultyAdjustment.adjust();
		} else {
			TransactionsBlock transactionBlock = BlockUtil.getLastTransactionBlock(blockDB, currentBlock);
			difficulty = (transactionBlock == null) ? propertiesBlock.getStartingDifficulty()
					: transactionBlock.getRewardTransaction().getDifficulty();
		}

		try {
			block.setMiner(WalletUtil.getPublicKeyFromString(blockchainProperties.getMiner()));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			throw new BusinessException("Invalid miner public key: " + blockchainProperties.getMiner());
		}

		BigDecimal missingSupply = propertiesBlock.getSupplyLimit()
				.subtract(currentCirculatingSupplyChainstateDB.get());

		// Miner reward
		RewardTransaction reward = new RewardTransaction(propertiesBlock.getCoinbase(),
				(missingSupply.compareTo(propertiesBlock.getReward()) > 0) ? propertiesBlock.getReward()
						: missingSupply,
				difficulty);
		reward.setTransactionId(transactionService.calulateHash(reward));
		reward.setOutput(new TransactionOutput(block.getMiner(), reward.getValue(), reward.getTransactionId()));
		block.getTransactions().add(reward);

		// Transactions from pool
		Iterator<SimpleTransaction> poolIterator = poolTransactions.iterator();
		try {
			do {
				SimpleTransaction transaction = poolIterator.next();
				try {
					blockService.addTransaction(block, transaction);
				} catch (BusinessException e) {
					LOG.debug("[Crypto] Jump and pop transaction due invalid transaction: " + e.getMessage());
				}
			} while (poolIterator.hasNext() && !blockService.isBlockFull(block));
		} catch (IOException e) {
			throw new BusinessException("Could not validate if block is full of transactions: " + e.getMessage());
		}
		LOG.trace("[Crypto] Transactions imported on block: " + block.getTransactions());

		block.setMerkleRoot(TransactionUtil.getMerkleRoot(block.getTransactions()));

		String target = StringUtil.getDificultyString(difficulty.intValue());
		block.setHash(blockService.calculateHash(block));
		while (!block.getHash().substring(0, difficulty.intValue()).equals(target)) {
			block.setNonce(block.getNonce() + 1);
			block.setHash(blockService.calculateHash(block));
		}

		blockStateManagement.foundBlock(block);
		LOG.info("[Crypto] Block Mined in " + (System.currentTimeMillis() - currentTimeInMillis) + " milliseconds: "
				+ block.getHash());
		LOG.debug("[Crypto] sending new block to peers...");
		for (SocketThread socketThread : SOCKET_THREADS.values()) {
			socketThread.send(BlockchainRequest.createBuilder().withService(Service.GET_BLOCK_RESPONSE)
					.withArguments(new BlockResponseArguments(new ArrayList<>(Arrays.asList(block)))).build());
		}
		mineBlock();
	}

}
