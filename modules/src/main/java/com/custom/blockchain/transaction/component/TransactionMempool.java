package com.custom.blockchain.transaction.component;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.exception.TransactionException;
import com.custom.blockchain.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class TransactionMempool {

	public static Set<SimpleTransaction> TRANSACTION_MEMPOOL = null;

	private BlockchainProperties blockchainProperties;

	private ObjectMapper objectMapper;

	public TransactionMempool(final BlockchainProperties blockchainProperties, final ObjectMapper objectMapper) {
		this.blockchainProperties = blockchainProperties;
		this.objectMapper = objectMapper;
	}

	public void getUnminedTransactions() throws TransactionException {
		try {
			TRANSACTION_MEMPOOL = objectMapper.readValue(
					FileUtil.readUnminedTransaction(blockchainProperties.getCoinName()),
					new TypeReference<Set<Transaction>>() {
					});
		} catch (IOException e) {
			throw new TransactionException("Could not get transactions from mempool: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param transaction
	 * @throws TransactionException
	 */
	public void updateMempool(SimpleTransaction transaction) throws TransactionException {
		TRANSACTION_MEMPOOL.add(transaction);
		try {
			FileUtil.addUnminedTransaction(blockchainProperties.getCoinName(),
					this.objectMapper.writeValueAsString(TRANSACTION_MEMPOOL));
		} catch (IOException e) {
			throw new TransactionException("Could not update to transaction mempool: " + e.getMessage());
		}
	}

}
