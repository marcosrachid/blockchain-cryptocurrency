package com.custom.blockchain.data;

public abstract class FlagAbstractLevelDB<Value> {
	
	public abstract Value get();

	public abstract void put(String value);

	public abstract void put(Value value);

	public abstract void delete();

}
