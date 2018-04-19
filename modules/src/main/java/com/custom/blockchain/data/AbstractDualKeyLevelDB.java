package com.custom.blockchain.data;

public abstract class AbstractDualKeyLevelDB<Key1, Value> extends AbstractLevelDB<Key1, Value> {

	public abstract Value get(String key);

	public abstract void delete(String key);

}
