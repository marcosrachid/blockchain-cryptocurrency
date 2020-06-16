package com.custom.blockchain.data;

import org.iq80.leveldb.DBIterator;

public abstract class FlagAbstractLevelDB<Key> extends AbstractCloseLevelDB {

	public abstract Boolean get(Key key);

	public abstract void put(Key key, Boolean Boolean);

	public abstract void delete(Key key);

	public abstract DBIterator iterator();

	public abstract Boolean next(final DBIterator iterator);

}
