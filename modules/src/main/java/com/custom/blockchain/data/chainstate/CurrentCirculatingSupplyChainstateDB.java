package com.custom.blockchain.data.chainstate;

import java.io.IOException;
import java.math.BigDecimal;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.custom.blockchain.data.PropertyAbstractLevelDB;
import com.custom.blockchain.data.exception.DatabaseException;
import com.custom.blockchain.util.NumberUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class CurrentCirculatingSupplyChainstateDB extends PropertyAbstractLevelDB<BigDecimal> {

	private static final Logger LOG = LoggerFactory.getLogger(CurrentCirculatingSupplyChainstateDB.class);

	private static final String KEY_BINDER = "S";

	private DB chainstateDb;

	public CurrentCirculatingSupplyChainstateDB(final @Qualifier("ChainStateDB") DB chainstateDb) {
		this.chainstateDb = chainstateDb;
	}

	@Override
	public BigDecimal get() {
		LOG.trace("[Crypto] ChainStateDB Get - Key: " + KEY_BINDER);
		try {
			return NumberUtil.toBigDecimal(chainstateDb.get(KEY_BINDER.getBytes()));
		} catch (DBException e) {
			LOG.debug("[Crypto] ChainStateDB Error from key [" + KEY_BINDER + "]: " + e.getMessage());
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void put(BigDecimal value) {
		LOG.trace("[Crypto] ChainStateDB Add Object - Key: " + KEY_BINDER + ", Value: " + value);
		try {
			chainstateDb.put(KEY_BINDER.getBytes(), NumberUtil.toByteArray(value));
		} catch (DBException e) {
			throw new DatabaseException(
					"Could not put data from key [" + KEY_BINDER + "] and Block [" + value + "]: " + e.getMessage());
		}

	}

	@Override
	public void delete() {
		LOG.trace("[Crypto] ChainStateDB Deleted - Key: " + KEY_BINDER);
		chainstateDb.delete(KEY_BINDER.getBytes());
	}

	public void add(BigDecimal value) {
		BigDecimal supply = get();
		put(supply.add(value));
	}

	public void subtract(BigDecimal value) {
		BigDecimal supply = get();
		put(supply.subtract(value));
	}

	@Override
	public void close() {
		LOG.info("[Crypto] closing ChainStateDB");
		try {
			chainstateDb.close();
			LOG.info("[Crypto] ChainStateDB closed");
		} catch (IOException e) {
			throw new DatabaseException("Could not close connection: " + e.getMessage());
		}
	}

}
