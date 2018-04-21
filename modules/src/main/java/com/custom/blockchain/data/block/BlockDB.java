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
import com.custom.blockchain.data.AbstractDualKeyLevelDB;
import com.custom.blockchain.data.exception.DatabaseException;
import com.custom.blockchain.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockDB extends AbstractDualKeyLevelDB<Long, AbstractBlock> {

	private static final Logger LOG = LoggerFactory.getLogger(BlockDB.class);

	private static final String KEY_1_BINDER = "b";

	private static final String KEY_2_BINDER = "B";

	private DB blockDb;

	private ObjectMapper objectMapper;

	public BlockDB(final @Qualifier("BlockDB") DB blockDb, final ObjectMapper objectMapper) {
		this.blockDb = blockDb;
		this.objectMapper = objectMapper;
	}

	@Override
	public AbstractBlock get(Long key) {
		try {
			LOG.trace("[Crypto] BlockDB Get - Key: " + KEY_1_BINDER + key);
			return objectMapper.readValue(StringUtil.decompress(blockDb.get(StringUtil.compress(KEY_1_BINDER + key))),
					AbstractBlock.class);
		} catch (DBException | IOException e) {
			LOG.debug("[Crypto] BlockDB Error from key [" + KEY_1_BINDER + key + "]: " + e.getMessage());
			return null;
		}
	}

	@Override
	public AbstractBlock get(String hash) {
		try {
			String key = StringUtil.decompress(blockDb.get(StringUtil.compress(KEY_2_BINDER + hash)));
			return get(Long.valueOf(key));
		} catch (DBException | IOException e) {
			LOG.debug("[Crypto] BlockDB Error from key [" + KEY_2_BINDER + hash + "]: " + e.getMessage());
			return null;
		}
	}

	@Override
	public void put(Long key, AbstractBlock value) {
		try {
			String v = objectMapper.writeValueAsString(value);
			LOG.trace("[Crypto] BlockDB Add Object - Key: " + KEY_1_BINDER + key + ", Value: " + v);
			blockDb.put(StringUtil.compress(KEY_1_BINDER + key), StringUtil.compress(v));
			blockDb.put(StringUtil.compress(KEY_2_BINDER + value.getHash()), StringUtil.compress(key.toString()));
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not put data from key [" + KEY_1_BINDER + key + "] and Block [" + value
					+ "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(Long key) {
		LOG.trace("[Crypto] BlockDB Deleted - Key: " + KEY_1_BINDER + key);
		try {
			blockDb.delete(StringUtil.compress(KEY_1_BINDER + key));
		} catch (DBException | IOException e) {
			throw new DatabaseException(
					"Could not delete data from key [" + KEY_1_BINDER + key + "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(String hash) {
		LOG.trace("[Crypto] BlockDB Deleted - Key: " + KEY_2_BINDER + hash);
		try {
			String key = StringUtil.decompress(blockDb.get(StringUtil.compress(KEY_2_BINDER + hash)));
			delete(Long.valueOf(key));
		} catch (DBException | IOException e) {
			throw new DatabaseException(
					"Could not delete data from key [" + KEY_2_BINDER + hash + "]: " + e.getMessage());
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
			if (key.startsWith(KEY_2_BINDER))
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
