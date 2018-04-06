package com.custom.blockchain.service;

import static com.custom.blockchain.node.NodeStateManagement.DIFFICULTY_ADJUSTMENT_BLOCK;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.node.component.DifficultyAdjustment;
import com.custom.blockchain.signature.SignatureManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.BlockUtil;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.util.WalletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class BlockService {

	private static final Logger LOG = LoggerFactory.getLogger(BlockService.class);

	private ObjectMapper objectMapper;

	private BlockchainProperties blockchainProperties;

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	private CurrentPropertiesBlockDB currentPropertiesBlockDB;

	private MempoolDB mempoolDB;

	private BlockStateManagement blockStateManagement;

	private SignatureManager signatureManager;

	private DifficultyAdjustment difficultyAdjustment;

	public BlockService(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final BlockDB blockDB, final CurrentBlockDB currentBlockDB,
			final CurrentPropertiesBlockDB currentPropertiesBlockDB, final MempoolDB mempoolDB,
			final BlockStateManagement blockStateManagement, final SignatureManager signatureManager,
			final DifficultyAdjustment difficultyAdjustment) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.mempoolDB = mempoolDB;
		this.blockStateManagement = blockStateManagement;
		this.signatureManager = signatureManager;
		this.difficultyAdjustment = difficultyAdjustment;
	}

	/**
	 * 
	 * @param block
	 * @throws BusinessException
	 */
	public void mineBlock() throws BusinessException {
		long currentTimeInMillis = System.currentTimeMillis();
		TransactionsBlock block = blockStateManagement.getNextBlock();

		Integer difficulty = null;
		if (block.getHeight() % DIFFICULTY_ADJUSTMENT_BLOCK == 0) {
			difficulty = difficultyAdjustment.adjust();
		} else {
			difficulty = BlockUtil.getLastTransactionBlock(blockDB, currentBlockDB.get()).getRewardTransaction()
					.getDifficulty();
		}

		String target = StringUtil.getDificultyString(difficulty.intValue());
		while (!block.getHash().substring(0, difficulty.intValue()).equals(target)) {
			block.setNonce(block.getNonce() + 1);
			block.calculateHash();
		}

		try {
			block.setMiner(WalletUtil.getPublicKeyFromString(blockchainProperties.getMiner()));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			throw new BusinessException("Invalid miner public key: " + blockchainProperties.getMiner());
		}

		// Miner reward
		RewardTransaction reward = new RewardTransaction(currentPropertiesBlockDB.get().getCoinbase(),
				currentPropertiesBlockDB.get().getReward(), difficulty);
		try {
			reward.setTransactionId(calulateRewardHash(reward));
		} catch (JsonProcessingException e) {
			throw new BusinessException(
					"Could not generate a txId for reward transaction: " + blockchainProperties.getMiner());
		}
		reward.setOutput(new TransactionOutput(block.getMiner(), reward.getValue(), reward.getTransactionId()));
		block.getTransactions().add(reward);

		// Transactions from pool
		DBIterator iterator = mempoolDB.iterator();
		try {
			while (iterator.hasNext() && !isBlockFull(block.getTransactions())) {
				addTransaction(block, mempoolDB.next(iterator));
			}
		} catch (UnsupportedEncodingException | JsonProcessingException e) {
			throw new BusinessException("Could not validate if block is full of transactions: " + e.getMessage());
		}
		LOG.trace("[Crypto] Transactions imported on block: " + block.getTransactions());

		block.setMerkleRoot(TransactionUtil.getMerkleRoot(block.getTransactions()));

		blockStateManagement.foundBlock(block);
		LOG.info("[Crypto] Block Mined in " + (System.currentTimeMillis() - currentTimeInMillis) + " milliseconds: "
				+ block.getHash());
		mineBlock();
	}

	/**
	 * 
	 * @param block
	 * @param transaction
	 * @throws BusinessException
	 */
	private void addTransaction(final TransactionsBlock block, SimpleTransaction transaction) throws BusinessException {
		if (transaction == null)
			throw new BusinessException("Non existent transaction");
		mempoolDB.delete(transaction.getTransactionId());
		processTransaction(transaction);
		block.getTransactions().add(transaction);
	}

	/**
	 * 
	 * @param transaction
	 * @param minimunTransaction
	 * @return
	 * @throws BusinessException
	 */
	private void processTransaction(final SimpleTransaction transaction) throws BusinessException {
		if (signatureManager.verifySignature(transaction) == false) {
			throw new BusinessException("Transaction Signature failed to verify. Transaction Discarded");
		}

		if (transaction.getValue().compareTo(currentPropertiesBlockDB.get().getMinimunTransaction()) < 0) {
			throw new BusinessException("Transaction sent funds are too low. Transaction Discarded");
		}

		// leftover
		BigDecimal leftOver = transaction.getInputsValue().subtract(transaction.getValue());
		TransactionOutput leftOutput = new TransactionOutput(transaction.getSender(), leftOver,
				transaction.getTransactionId());
		BigDecimal total = leftOutput.getValue().add(transaction.getOutputsValue());

		if (transaction.getInputsValue().compareTo(total) != 0) {
			throw new BusinessException("Transaction Inputs total[" + transaction.getInputsValue().toPlainString()
					+ "] value differs from Transaction Outputs total[" + total.toPlainString()
					+ "] value. Transaction Discarded");
		}

		transaction.getOutputs().add(leftOutput);
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 * @throws JsonProcessingException
	 */
	private String calulateRewardHash(RewardTransaction transaction) throws JsonProcessingException {
		Transaction.sequence++;
		return DigestUtil.applySha256(transaction.getCoinbase() + transaction.getValue().setScale(8).toString()
				+ transaction.getTimeStamp() + Transaction.sequence);
	}

	/**
	 * 
	 * @param transactions
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws JsonProcessingException
	 */
	private boolean isBlockFull(final Set<Transaction> transactions)
			throws UnsupportedEncodingException, JsonProcessingException {
		return StringUtil.sizeof(objectMapper.writeValueAsString(transactions)) > currentPropertiesBlockDB.get()
				.getBlockSize();
	}

}
