package com.custom.blockchain.data.chainstate;

import java.security.PublicKey;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.custom.blockchain.data.AbstractLevelDB;
import com.custom.blockchain.data.exception.LevelDBException;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.WalletUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ChainstateDB extends AbstractLevelDB<PublicKey, TransactionOutput> {

	private DB chainstateDb;

	private ObjectMapper objectMapper;

	public ChainstateDB(final @Qualifier("ChainStateDB") DB chainstateDb, final ObjectMapper objectMapper) {
		this.chainstateDb = chainstateDb;
		this.objectMapper = objectMapper;
	}

	@Override
	public TransactionOutput get(String key) {
		try {
			return objectMapper
					.readValue(chainstateDb.get(key.getBytes()), TransactionOutput.TransactionOutputSerializable.class)
					.unserializable();
		} catch (Exception e) {
			throw new LevelDBException("Could not get data from key " + key + ": " + e.getMessage());
		}
	}

	@Override
	public void put(String key, String value) {
		chainstateDb.put(key.getBytes(), value.getBytes());
	}

	@Override
	public void put(String key, TransactionOutput value) {
		try {
			chainstateDb.put(key.getBytes(), objectMapper.writeValueAsBytes(value.serializable()));
		} catch (DBException | JsonProcessingException e) {
			throw new LevelDBException("Could not put data from key [" + key + "] and TransactionOutput [" + value
					+ "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(String key) {
		chainstateDb.delete(key.getBytes());
	}

	@Override
	public TransactionOutput get(PublicKey key) {
		try {
			return objectMapper.readValue(chainstateDb.get(getKeyByteArray(key)),
					TransactionOutput.TransactionOutputSerializable.class).unserializable();
		} catch (Exception e) {
			throw new LevelDBException("Could not get data from key " + key + ": " + e.getMessage());
		}
	}

	@Override
	public void put(PublicKey key, String value) {
		chainstateDb.put(getKeyByteArray(key), value.getBytes());

	}

	@Override
	public void put(PublicKey key, TransactionOutput value) {
		try {
			chainstateDb.put(getKeyByteArray(key), objectMapper.writeValueAsBytes(value.serializable()));
		} catch (DBException | JsonProcessingException e) {
			throw new LevelDBException("Could not put data from key [" + key + "] and TransactionOutput [" + value
					+ "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(PublicKey key) {
		chainstateDb.delete(getKeyByteArray(key));
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private byte[] getKeyByteArray(PublicKey key) {
		return WalletUtil.getStringFromKey(key).getBytes();
	}

}
