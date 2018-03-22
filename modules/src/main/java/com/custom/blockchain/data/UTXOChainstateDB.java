package com.custom.blockchain.data;

import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.custom.blockchain.data.exception.LevelDBException;
import com.custom.blockchain.transaction.TransactionOutput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class UTXOChainstateDB extends AbstractLevelDB<String, TransactionOutput> {

	private static final Logger LOG = LoggerFactory.getLogger(UTXOChainstateDB.class);
	
	private static final String KEY_BINDER = "c";

	private DB chainstateDb;

	private ObjectMapper objectMapper;

	public UTXOChainstateDB(final @Qualifier("ChainStateDB") DB chainstateDb, final ObjectMapper objectMapper) {
		this.chainstateDb = chainstateDb;
		this.objectMapper = objectMapper;
	}

	@Override
	public TransactionOutput get(String key) {
		try {
			LOG.trace("[Crypto] ChainstateDB Get - Key: " + key);
			return objectMapper.readValue(chainstateDb.get((KEY_BINDER + key).getBytes()), TransactionOutput.class);
		} catch (Exception e) {
			throw new LevelDBException("Could not get data from key " + key + ": " + e.getMessage());
		}
	}

	@Override
	public void put(String key, String value) {
		LOG.trace("[Crypto] ChainstateDB Add String - Key: " + key + ", Value: " + value);
		chainstateDb.put((KEY_BINDER + key).getBytes(), value.getBytes());
	}

	@Override
	public void put(String key, TransactionOutput value) {
		try {
			String v = objectMapper.writeValueAsString(value);
			LOG.trace("[Crypto] ChainstateDB Add Object - Key: " + key + ", Value: " + v);
			chainstateDb.put((KEY_BINDER + key).getBytes(), v.getBytes());
		} catch (DBException | JsonProcessingException e) {
			throw new LevelDBException("Could not put data from key [" + key + "] and TransactionOutput [" + value
					+ "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(String key) {
		LOG.trace("[Crypto] ChainstateDB Deleted - Key: " + key);
		chainstateDb.delete((KEY_BINDER + key).getBytes());
	}

	@Override
	public DBIterator iterator() {
		DBIterator iterator = chainstateDb.iterator();
		iterator.seekToFirst();
		return iterator;
	}

	@Override
	public TransactionOutput next(final DBIterator iterator) {
		try {
			Entry<byte[], byte[]> entry = iterator.next();
			LOG.trace("[Crypto] ChainstateDB Current Iterator - Key: " + new String(entry.getKey()) + ", Value: "
					+ new String(entry.getValue()));
			return objectMapper.readValue(entry.getValue(), TransactionOutput.class);
		} catch (Exception e) {
			throw new LevelDBException("Could not get data from iterator: " + e.getMessage());
		}
	}

}
