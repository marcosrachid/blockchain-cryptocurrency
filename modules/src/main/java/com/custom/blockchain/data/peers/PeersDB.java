package com.custom.blockchain.data.peers;

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
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PeersDB extends AbstractLevelDB<String, Peer> {

	private static final Logger LOG = LoggerFactory.getLogger(PeersDB.class);

	private static final String KEY_BINDER = "p";

	private DB peersDB;

	private ObjectMapper objectMapper;

	public PeersDB(final @Qualifier("PeersDB") DB peersDB, final ObjectMapper objectMapper) {
		this.peersDB = peersDB;
		this.objectMapper = objectMapper;
	}

	@Override
	public Peer get(String key) {
		try {
			LOG.trace("[Crypto] PeersDB Get - Key: " + KEY_BINDER + key);
			return objectMapper.readValue(StringUtil.decompress(peersDB.get(StringUtil.compress(KEY_BINDER + key))),
					Peer.class);
		} catch (Exception e) {
			throw new DatabaseException("Could not get data from key " + key + ": " + e.getMessage());
		}
	}

	@Override
	public void put(String key, Peer value) {
		try {
			String v = objectMapper.writeValueAsString(value);
			LOG.trace("[Crypto] PeersDB Add Object - Key: " + KEY_BINDER + key + ", Value: " + v);
			peersDB.put(StringUtil.compress(KEY_BINDER + key), StringUtil.compress(v));
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not put data from key [" + KEY_BINDER + key + "] and Peer [" + value
					+ "]: " + e.getMessage());
		}
	}

	@Override
	public void delete(String key) {
		LOG.trace("[Crypto] PeersDB Deleted - Key: " + key);
		try {
			peersDB.delete(StringUtil.compress(KEY_BINDER + key));
		} catch (DBException | IOException e) {
			throw new DatabaseException("Could not delete data from key [" + KEY_BINDER + key + "]: " + e.getMessage());
		}
	}

	@Override
	public DBIterator iterator() {
		DBIterator iterator = peersDB.iterator();
		iterator.seekToFirst();
		return iterator;
	}

	@Override
	public Peer next(DBIterator iterator) {
		try {
			Entry<byte[], byte[]> entry = iterator.next();
			String value = StringUtil.decompress(entry.getValue());
			LOG.trace("[Crypto] PeersDB Current Iterator - Key: " + KEY_BINDER + new String(entry.getKey())
					+ ", Value: " + value);
			return objectMapper.readValue(value, Peer.class);
		} catch (Exception e) {
			throw new DatabaseException("Could not get data from iterator: " + e.getMessage());
		}
	}

}
