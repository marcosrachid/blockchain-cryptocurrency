package com.custom.blockchain.transaction.component;

import java.util.HashSet;
import java.util.Set;

import org.iq80.leveldb.DBIterator;
import org.springframework.stereotype.Component;

import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.exception.TransactionException;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class TransactionMempool {

	private MempoolDB mempoolDB;

	public TransactionMempool(final MempoolDB mempoolDB) {
		this.mempoolDB = mempoolDB;
	}

	/**
	 * 
	 * @throws TransactionException
	 */
	public void restartMempool() throws TransactionException {
		DBIterator iterator = mempoolDB.iterator();
		while (iterator.hasNext()) {
			SimpleTransaction transaction = mempoolDB.next(iterator);
			mempoolDB.delete(transaction.getTransactionId());
		}
	}

	/**
	 * 
	 * @throws TransactionException
	 */
	public Set<SimpleTransaction> getUnminedTransactions() throws TransactionException {
		Set<SimpleTransaction> mempoolTransactions = new HashSet<>();
		DBIterator iterator = mempoolDB.iterator();
		while (iterator.hasNext()) {
			mempoolTransactions.add(mempoolDB.next(iterator));
		}
		return mempoolTransactions;
	}

	/**
	 * 
	 * @param transaction
	 * @throws TransactionException
	 */
	public void updateMempool(SimpleTransaction transaction) throws TransactionException {
		mempoolDB.put(transaction.getTransactionId(), transaction);
	}

}
