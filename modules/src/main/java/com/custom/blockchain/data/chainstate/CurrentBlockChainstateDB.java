package com.custom.blockchain.data.chainstate;

import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.data.PropertyAbstractLevelDB;
import com.custom.blockchain.data.exception.DatabaseException;
import com.custom.blockchain.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class CurrentBlockChainstateDB extends PropertyAbstractLevelDB<AbstractBlock> {

	private static final Logger LOG = LoggerFactory.getLogger(CurrentBlockChainstateDB.class);

	private static final String KEY_BINDER = "B";

	private DB chainstateDb;

	private ObjectMapper objectMapper;

	public CurrentBlockChainstateDB(final ObjectMapper objectMapper, final @Qualifier("ChainStateDB") DB chainstateDb) {
		this.objectMapper = objectMapper;
		this.chainstateDb = chainstateDb;
	}

	@Override
	public AbstractBlock get() {
		LOG.trace("[Crypto] ChainStateDB Get - Key: " + KEY_BINDER);
		try {
			return objectMapper.readValue(StringUtil.decompress(chainstateDb.get(KEY_BINDER.getBytes())),
					AbstractBlock.class);
		} catch (DBException | IOException e) {
			LOG.debug("[Crypto] ChainStateDB Error from key [" + KEY_BINDER + "]: " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void put(AbstractBlock value) {
		try {
			String v = objectMapper.writeValueAsString(value);
			LOG.trace("[Crypto] ChainStateDB Add Object - Key: " + KEY_BINDER + ", Value: " + v);
			chainstateDb.put(KEY_BINDER.getBytes(), StringUtil.compress(v));
		} catch (DBException | IOException e) {
			throw new DatabaseException(
					"Could not put data from key [" + KEY_BINDER + "] and Block [" + value + "]: " + e.getMessage());
		}
	}

	@Override
	public void delete() {
		LOG.trace("[Crypto] ChainStateDB Deleted - Key: " + KEY_BINDER);
		chainstateDb.delete(KEY_BINDER.getBytes());
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
