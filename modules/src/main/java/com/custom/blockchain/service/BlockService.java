package com.custom.blockchain.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.signature.SignatureManager;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BlockService {

	private static final Logger LOG = LoggerFactory.getLogger(BlockService.class);

	private ObjectMapper objectMapper;

	private CurrentBlockDB currentBlockDB;

	private CurrentPropertiesBlockDB currentPropertiesBlockDB;

	private MempoolDB mempoolDB;

	private SignatureManager signatureManager;

	public BlockService(final ObjectMapper objectMapper, final CurrentBlockDB currentBlockDB,
			final CurrentPropertiesBlockDB currentPropertiesBlockDB, final MempoolDB mempoolDB,
			final SignatureManager signatureManager) {
		this.objectMapper = objectMapper;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.mempoolDB = mempoolDB;
		this.signatureManager = signatureManager;
	}

	/**
	 * 
	 */
	public String calculateHash(TransactionsBlock transactionsBlock) {
		return DigestUtil.applySha256(DigestUtil.applySha256(transactionsBlock.getPreviousHash()
				+ transactionsBlock.getPropertiesHash() + transactionsBlock.getTimeStamp()
				+ transactionsBlock.getNonce() + transactionsBlock.getMerkleRoot()));
	}

	/**
	 * 
	 */
	public String calculateHash(PropertiesBlock propertiesBlock) {
		return DigestUtil.applySha256(DigestUtil
				.applySha256(propertiesBlock.getPreviousHash() + propertiesBlock.getMinimunTransaction().toPlainString()
						+ propertiesBlock.getCoinLimit().toPlainString() + propertiesBlock.getMiningTimeRate()
						+ propertiesBlock.getReward().toPlainString() + propertiesBlock.getBlockSize()
						+ propertiesBlock.getCoinbase()));
	}

	/**
	 * 
	 * @param transactionsBlock
	 * @return
	 */
	public boolean isBlockCompatible(TransactionsBlock transactionsBlock) {
		AbstractBlock currentBlock = currentBlockDB.get();
		PropertiesBlock propertiesBlock = currentPropertiesBlockDB.get();
		LOG.trace(
				"Coming Transaction parameters: previousHash[{}], propertiesHash[{}], timestamp[{}], nonce[{}], merkleroot[{}]",
				transactionsBlock.getPreviousHash(), transactionsBlock.getPropertiesHash(),
				transactionsBlock.getTimeStamp(), transactionsBlock.getNonce(), transactionsBlock.getMerkleRoot());
		LOG.trace("Testing parameters: previousHash[{}], propertiesHash[{}], timestamp[{}], nonce[{}], merkleroot[{}]",
				currentBlock.getHash(), propertiesBlock.getHash(), transactionsBlock.getTimeStamp(),
				transactionsBlock.getNonce(), transactionsBlock.getMerkleRoot());
		LOG.debug("Coming Transaction hash: {}, Test hash: {}", transactionsBlock.getHash(),
				DigestUtil.applySha256(DigestUtil.applySha256(
						currentBlock.getHash() + propertiesBlock.getHash() + transactionsBlock.getTimeStamp()
								+ transactionsBlock.getNonce() + transactionsBlock.getMerkleRoot())));
		return transactionsBlock.getHash()
				.equals(DigestUtil.applySha256(DigestUtil.applySha256(
						currentBlock.getHash() + propertiesBlock.getHash() + transactionsBlock.getTimeStamp()
								+ transactionsBlock.getNonce() + transactionsBlock.getMerkleRoot())));
	}

	/**
	 * 
	 * @param block
	 * @param transaction
	 * @throws BusinessException
	 */
	public void addTransaction(final TransactionsBlock block, SimpleTransaction transaction) throws BusinessException {
		if (transaction == null)
			throw new BusinessException("Non existent transaction");
		mempoolDB.delete(transaction.getTransactionId());
		processTransaction(transaction);
		block.getTransactions().add(transaction);
	}

	/**
	 * 
	 * @param transactions
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws JsonProcessingException
	 */
	public boolean isBlockFull(final AbstractBlock block) throws IOException {
		return StringUtil.sizeof(StringUtil.compress(objectMapper.writeValueAsString(block))) > currentPropertiesBlockDB
				.get().getBlockSize();
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

}
