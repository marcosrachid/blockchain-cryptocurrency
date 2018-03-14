package com.custom.blockchain.data;

import org.iq80.leveldb.DBIterator;

public abstract class AbstractLevelDB<Key, Value> {

	public abstract Value get(String key);

	public abstract void put(String key, String value);

	public abstract void put(String key, Value value);

	public abstract void delete(String key);

	public abstract Value get(Key key);

	public abstract void put(Key key, String value);

	public abstract void put(Key key, Value value);

	public abstract void delete(Key key);
	
	public abstract DBIterator iterator();
	
	public abstract Value next(final DBIterator iterator);

}
