package com.custom.blockchain.service;

import static com.custom.blockchain.node.NodeStateManagement.DIFFICULTY;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.signature.SignatureManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.transaction.exception.TransactionException;
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

	private UTXOChainstateDB utxoChainstateDb;

	private MempoolDB mempoolDB;

	private BlockStateManagement blockStateManagement;

	private SignatureManager signatureManager;

	public BlockService(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final UTXOChainstateDB utxoChainstateDb, final MempoolDB mempoolDB,
			final BlockStateManagement blockStateManagement, final SignatureManager signatureManager) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.utxoChainstateDb = utxoChainstateDb;
		this.mempoolDB = mempoolDB;
		this.blockStateManagement = blockStateManagement;
		this.signatureManager = signatureManager;
	}

	/**
	 * 
	 * @param block
	 * @throws BlockException
	 */
	public void mineBlock() throws BlockException {
		Block block = blockStateManagement.getNextBlock();
		String target = StringUtil.getDificultyString(DIFFICULTY);
		while (!block.getHash().substring(0, DIFFICULTY).equals(target)) {
			block.setNonce(block.getNonce() + 1);
			block.calculateHash();
		}

		block.setDifficulty(DIFFICULTY);
		try {
			block.setMiner(WalletUtil.getPublicKeyFromString(blockchainProperties.getMiner()));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			throw new BlockException("Invalid miner public key: " + blockchainProperties.getMiner());
		}

		Set<Transaction> transactions = new HashSet<>();

		// Miner reward
		RewardTransaction reward = new RewardTransaction(blockchainProperties.getCoinbase(),
				blockchainProperties.getReward());
		try {
			reward.setTransactionId(calulateRewardHash(reward));
		} catch (JsonProcessingException e) {
			throw new BlockException(
					"Could not generate a txId for reward transaction: " + blockchainProperties.getMiner());
		}
		reward.setOutput(new TransactionOutput(block.getMiner(), reward.getValue(), reward.getTransactionId()));
		transactions.add(reward);

		// Transactions from pool
		DBIterator iterator = mempoolDB.iterator();
		try {
			while (iterator.hasNext() && !isBlockFull(transactions)) {
				transactions.add(mempoolDB.next(iterator));
			}
		} catch (UnsupportedEncodingException | JsonProcessingException e) {
			throw new BlockException("Could not validate if block is full of transactions: " + e.getMessage());
		}
		LOG.trace("[Crypto] Transactions imported on block: " + transactions);
		block.setMerkleRoot(TransactionUtil.getMerkleRoot(transactions));
		for (Transaction t : transactions) {
			addTransaction(block, t);
		}
		blockStateManagement.foundBlock(block);
		LOG.info("[Crypto] Block Mined!!! : " + block.getHash());
		mineBlock();
	}

	/**
	 * 
	 * @param block
	 * @param transaction
	 * @throws BlockException
	 */
	private void addTransaction(final Block block, Transaction transaction) throws BlockException {
		if (transaction instanceof SimpleTransaction) {
			addTransaction(block, (SimpleTransaction) transaction);
		}
		if (transaction instanceof RewardTransaction) {
			addTransaction(block, (RewardTransaction) transaction);
		}
	}

	/**
	 * 
	 * @param block
	 * @param transaction
	 * @throws TransactionException
	 */
	private void addTransaction(final Block block, RewardTransaction transaction) throws BlockException {
		if (transaction == null)
			throw new BlockException("Non existent transaction");
		processTransaction(transaction);
		block.getTransactions().add(transaction);
	}

	/**
	 * 
	 * @param block
	 * @param transaction
	 * @throws TransactionException
	 */
	private void addTransaction(final Block block, SimpleTransaction transaction) throws BlockException {
		if (transaction == null)
			throw new BlockException("Non existent transaction");
		processTransaction(transaction);
		block.getTransactions().add(transaction);
	}

	/**
	 * 
	 * @param transaction
	 * @throws BlockException
	 */
	private void processTransaction(RewardTransaction transaction) throws BlockException {
		if (transaction.getValue().compareTo(blockchainProperties.getMinimunTransaction()) < 0) {
			throw new BlockException("Transaction sent funds are too low. Transaction Discarded");
		}

		utxoChainstateDb.add(transaction.getOutput().getReciepient(), transaction.getOutput());
	}

	/**
	 * 
	 * @param transaction
	 * @param minimunTransaction
	 * @return
	 * @throws TransactionException
	 */
	private void processTransaction(SimpleTransaction transaction) throws BlockException {

		if (signatureManager.verifiySignature(transaction) == false) {
			mempoolDB.delete(transaction.getTransactionId());
			throw new BlockException("Transaction Signature failed to verify. Transaction Discarded");
		}

		if (transaction.getValue().compareTo(blockchainProperties.getMinimunTransaction()) < 0) {
			mempoolDB.delete(transaction.getTransactionId());
			throw new BlockException("Transaction sent funds are too low. Transaction Discarded");
		}

		BigDecimal leftOver = transaction.getInputsValue().subtract(transaction.getValue());
		TransactionOutput leftOutput = new TransactionOutput(transaction.getSender(), leftOver,
				transaction.getTransactionId());
		BigDecimal total = leftOutput.getValue().add(transaction.getOutputsValue());

		if (transaction.getInputsValue().compareTo(total) != 0) {
			mempoolDB.delete(transaction.getTransactionId());
			throw new BlockException("Transaction Inputs total[" + transaction.getInputsValue().toPlainString()
					+ "] value differs from Transaction Outputs total[" + total.toPlainString()
					+ "] value. Transaction Discarded");
		}
		utxoChainstateDb.leftOver(leftOutput.getReciepient(), leftOutput);

		for (TransactionOutput o : transaction.getOutputs()) {
			utxoChainstateDb.add(o.getReciepient(), o);
		}

		mempoolDB.delete(transaction.getTransactionId());
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
		return StringUtil.sizeof(objectMapper.writeValueAsString(transactions)) > blockchainProperties.getBlockSize();
	}

}
