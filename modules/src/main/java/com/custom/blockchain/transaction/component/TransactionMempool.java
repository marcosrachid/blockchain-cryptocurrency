package com.custom.blockchain.transaction.component;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

	@Value("${application.name:'RachidCoin'}")
	private String coinName;

	private ObjectMapper objectMapper;

	public TransactionMempool(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public void getUnminedTransactions() throws TransactionException {
		try {
			TRANSACTION_MEMPOOL = objectMapper.readValue(FileUtil.readUnminedTransaction(coinName),
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
			FileUtil.addUnminedTransaction(coinName, this.objectMapper.writeValueAsString(TRANSACTION_MEMPOOL));
		} catch (IOException e) {
			throw new TransactionException("Could not update to transaction mempool: " + e.getMessage());
		}
	}

}
