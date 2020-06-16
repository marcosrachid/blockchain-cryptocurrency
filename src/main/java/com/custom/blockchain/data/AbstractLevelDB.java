package com.custom.blockchain.data;

import org.iq80.leveldb.DBIterator;

public abstract class AbstractLevelDB<Key, Value> extends AbstractCloseLevelDB {

	public abstract Value get(Key key);

	public abstract void put(Key key, Value value);

	public abstract void delete(Key key);

	public abstract DBIterator iterator();

	public abstract Value next(final DBIterator iterator);

}
