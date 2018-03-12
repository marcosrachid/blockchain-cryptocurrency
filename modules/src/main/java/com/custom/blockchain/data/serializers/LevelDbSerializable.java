package com.custom.blockchain.data.serializers;

import java.io.Serializable;

public interface LevelDbSerializable<T> extends Serializable {

	public T unserializable() throws Exception;

}
