package com.custom.blockchain.data.block;

import java.io.IOException;
import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.data.AbstractLevelDB;
import com.custom.blockchain.data.exception.DatabaseException;
import com.custom.blockchain.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockDB extends AbstractLevelDB<Long, AbstractBlock> {

	private static final Logger LOG = LoggerFactory.getLogger(BlockDB.class);

	private static final String KEY_BINDER = "b";

	private static final String EXCLUDING_KEY_BINDER = "B";

	private DB blockDb;

	private ObjectMapper objectMapper;

	public BlockDB(final @Qualifier("BlockDB") DB blockDb, final ObjectMapper objectMapper) {
		this.blockDb = blockDb;
		this.objectMapper = objectMapper;
	}

	@Override
	public AbstractBlock get(Long key) {
		try {
			LOG.trace("[Crypto] BlockDB Get - Key: " + KEY_BINDER + key);
			return objectMapper.readValue(StringUtil.decompress(blockDb.get(StringUtil.compress(KEY_BINDER + key))),
					AbstractBlock.class);
		} catch (DBException | IOException e) {
			return null;
		}
	}

	@Override
	public void put(Long key, AbstractBlock value) {
		try {
			String v = objectMapper.writeValueAsString(value);
			LOG.trace("[Crypto] BlockDB Add Object - Key: " + KEY_BINDER + key + ", Value: " + v);
			blockDb.put(StringUtil.compress(KEY_BINDER + key), StringUtil.compress(v));
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not put data from key [" + KEY_BINDER + key + "] and Block [" + value
					+ "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(Long key) {
		LOG.trace("[Crypto] BlockDB Deleted - Key: " + key);
		try {
			blockDb.delete(StringUtil.compress(KEY_BINDER + key));
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not delete data from key [" + KEY_BINDER + key + "]: " + e.getMessage());
		}
	}

	@Override
	public DBIterator iterator() {
		DBIterator iterator = blockDb.iterator();
		iterator.seekToFirst();
		return iterator;
	}

	@Override
	public AbstractBlock next(DBIterator iterator) {
		try {
			Entry<byte[], byte[]> entry = iterator.next();
			String key = StringUtil.decompress(entry.getKey());
			if (key.startsWith(EXCLUDING_KEY_BINDER))
				return next(iterator);
			String value = StringUtil.decompress(entry.getValue());
			LOG.trace("[Crypto] BlockDB Current Iterator - Key: " + key + ", Value: " + value);
			return objectMapper.readValue(value, AbstractBlock.class);
		} catch (Exception e) {
			throw new DatabaseException("Could not get data from iterator: " + e.getMessage());
		}
	}

	@Override
	public void close() {
		LOG.info("[Crypto] closing BlockDB");
		try {
			blockDb.close();
			LOG.info("[Crypto] BlockDB closed");
		} catch (IOException e) {
			throw new DatabaseException("Could not close connection: " + e.getMessage());
		}
	}

}
