package com.custom.blockchain.data;

public abstract class PropertyAbstractLevelDB<Value> extends AbstractCloseLevelDB {

	public abstract Value get();

	public abstract void put(Value value);

	public abstract void delete();

}
