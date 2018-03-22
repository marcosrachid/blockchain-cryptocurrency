package com.custom.blockchain.data;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.data.exception.LevelDBException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class GenesisBlockChainstateDB extends FlagAbstractLevelDB<Block> {

	private static final Logger LOG = LoggerFactory.getLogger(UTXOChainstateDB.class);

	private static final String KEY_BINDER = "G";

	private DB chainstateDb;

	private ObjectMapper objectMapper;

	public GenesisBlockChainstateDB(final @Qualifier("ChainStateDB") DB chainstateDb, final ObjectMapper objectMapper) {
		this.chainstateDb = chainstateDb;
		this.objectMapper = objectMapper;
	}

	@Override
	public Block get() {
		LOG.trace("[Crypto] ChainstateDB Get - Key: " + KEY_BINDER);
		try {
			return objectMapper.readValue(chainstateDb.get(KEY_BINDER.getBytes()), Block.class);
		} catch (Exception e) {
			throw new LevelDBException("Could not get data from key " + KEY_BINDER + ": " + e.getMessage());
		}
	}

	@Override
	public void put(String value) {
		LOG.trace("[Crypto] ChainstateDB Add String - Key: " + KEY_BINDER + ", Value: " + value);
		chainstateDb.put(KEY_BINDER.getBytes(), value.getBytes());
	}

	@Override
	public void put(Block value) {
		try {
			String v = objectMapper.writeValueAsString(value);
			LOG.trace("[Crypto] ChainstateDB Add Object - Key: " + KEY_BINDER + ", Value: " + v);
			chainstateDb.put(KEY_BINDER.getBytes(), v.getBytes());
		} catch (DBException | JsonProcessingException e) {
			throw new LevelDBException(
					"Could not put data from key [" + KEY_BINDER + "] and Block [" + value + "]: " + e.getMessage());
		}
	}

	@Override
	public void delete() {
		LOG.trace("[Crypto] ChainstateDB Deleted - Key: " + KEY_BINDER);
		chainstateDb.delete(KEY_BINDER.getBytes());
	}

}
