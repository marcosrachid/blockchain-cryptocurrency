package com.custom.blockchain.data.mempool;

import java.io.IOException;
import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.custom.blockchain.data.AbstractLevelDB;
import com.custom.blockchain.data.exception.DatabaseException;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MempoolDB extends AbstractLevelDB<String, SimpleTransaction> {

	private static final Logger LOG = LoggerFactory.getLogger(MempoolDB.class);

	private DB mempoolDB;

	private ObjectMapper objectMapper;

	public MempoolDB(final @Qualifier("MempoolDB") DB mempoolDB, final ObjectMapper objectMapper) {
		this.mempoolDB = mempoolDB;
		this.objectMapper = objectMapper;
	}

	@Override
	public SimpleTransaction get(String key) {
		try {
			LOG.trace("[Crypto] MempoolDB Get - Key: " + key);
			return objectMapper.readValue(StringUtil.decompress(mempoolDB.get(StringUtil.compress(key))),
					SimpleTransaction.class);
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not get data from key " + key + ": " + e.getMessage());
		}
	}

	@Override
	public void put(String key, SimpleTransaction value) {
		try {
			String v = objectMapper.writeValueAsString(value);
			LOG.trace("[Crypto] MempoolDB Add Object - Key: " + key + ", Value: " + v);
			mempoolDB.put(StringUtil.compress(key), StringUtil.compress(v));
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not put data from key [" + key + "] and SimpleTransaction [" + value
					+ "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(String key) {
		LOG.trace("[Crypto] MempoolDB Deleted - Key: " + key);
		try {
			mempoolDB.delete(StringUtil.compress(key));
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not delete data from key [" + key + "]: " + e.getMessage());
		}
	}

	@Override
	public DBIterator iterator() {
		DBIterator iterator = mempoolDB.iterator();
		iterator.seekToFirst();
		return iterator;
	}

	@Override
	public SimpleTransaction next(DBIterator iterator) {
		try {
			Entry<byte[], byte[]> entry = iterator.next();
			String key = StringUtil.decompress(entry.getKey());
			String value = StringUtil.decompress(entry.getValue());
			LOG.trace("[Crypto] MempoolDB Current Iterator - Key: " + key + ", Value: " + value);
			return objectMapper.readValue(value, SimpleTransaction.class);
		} catch (Exception e) {
			throw new DatabaseException("Could not get data from iterator: " + e.getMessage());
		}
	}

}
